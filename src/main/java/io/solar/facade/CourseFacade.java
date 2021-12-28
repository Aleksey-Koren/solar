package io.solar.facade;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.mapper.CourseMapper;
import io.solar.service.CourseService;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseFacade {

    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public void updateCourseChain(CourseDto dto) {
        if(dto.getNextId() == null) {
            extendCourseChain(dto);
        }else{
            adjustCourseChain(dto);
        }
    }

    private void extendCourseChain(CourseDto dto) {
        Course last = courseMapper.toEntity(dto);
        List<Course> courses = last.getObject().getCourses();
        if (courses.size() > 0) {
            Course previousLast = findLastCourse(courses);
            last.setPrevious(previousLast);
            last.setExpireAt(previousLast.getExpireAt().plusMillis(last.getTime()));
            courseService.save(last);
            previousLast.setNext(last);
            courseService.save(previousLast);
        }
        Instant now = Instant.now();
        last.setCreatedAt(now);
        last.setExpireAt(now.minusMillis(last.getTime()));
        courseService.save(last);
    }

    private Course findLastCourse(List<Course> courses) {
        List<Course> withoutNext = courses.stream()
                                          .filter(s -> s.getNext() == null)
                                          .collect(Collectors.toList());
        if(withoutNext.size() != 1) {
            throw new ServiceException(String
                    .format("Flying object must contains exactly one course chain segment without next field. But it has %d.", withoutNext.size()));
        }

        return withoutNext.get(0);
    }

    private void adjustCourseChain(CourseDto dto) {
        Course newCourse = courseMapper.toEntity(dto);
        Optional<Course> previousOptional = courseService.findByNext(courseService.findById(dto.getNextId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("There is no course with such id in database. id = %d", dto.getNextId()))));
        if(previousOptional.isPresent()) {
            Course previous = previousOptional.get();
            Course next = newCourse.getNext();
            newCourse.setPrevious(previous);
            courseService.save(newCourse);
            previous.setNext(newCourse);
            courseService.save(previous);
            next.setPrevious(newCourse);
            courseService.save(next);
        }else{
            courseService.save(newCourse);
        }

        recalculateExpirationsFrom(newCourse);
    }

    private void recalculateExpirationsFrom(Course course) {
        Course current = course;
        do{
            current.setExpireAt(current.getPrevious().getExpireAt().plusMillis(current.getTime()));
            current = current.getNext();
        }while(current != null);
    }

    public void deleteCourse(Course course) {
        Course previous = course.getPrevious();
        Course next = course.getNext();
        if(previous != null) {
            previous.setNext(course.getNext());
            courseService.save(previous);
        }
        if(next != null) {
            next.setPrevious(previous);
            recalculateExpirationsFrom(next);
        }

        courseService.deleteById(course.getId());
    }
}
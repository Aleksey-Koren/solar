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
            courseService.save(last);
            previousLast.setNext(last);
            courseService.save(previousLast);
        }
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
            courseService.save(newCourse);
            previous.setNext(newCourse);
            courseService.save(previous);
        }else{
            courseService.save(newCourse);
        }
    }

    public void deleteCourse(Course course) {
        Optional<Course> previousOptional = courseService.findByNext(course);
        if(previousOptional.isPresent()) {
            Course previous = previousOptional.get();
            previous.setNext(course.getNext());
            courseService.save(previous);
        }

        courseService.deleteById(course.getId());
    }
}
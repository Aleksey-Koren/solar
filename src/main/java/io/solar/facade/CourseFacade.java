package io.solar.facade;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.mapper.CourseMapper;
import io.solar.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

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
        Instant now =Instant.now();
        Optional<Course> previousLast = courseService.findLastCourse(last.getObject());
        if (previousLast.isPresent()) {
            Course previous = previousLast.get();
            last.setPrevious(previous);
            last.setExpireAt(previous.getExpireAt().isAfter(now)
                    ? previous.getExpireAt().plusMillis(last.getTime())
                    : Instant.now().plusMillis(last.getTime()));
            courseService.save(last);
            previous.setNext(last);
            courseService.save(previous);
        }else{
            last.setCreatedAt(now);
            last.setExpireAt(now.plusMillis(last.getTime()));
        }

        courseService.save(last);
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

        recalculateExpirationsFromAdjust(newCourse);
    }

    private void recalculateExpirationsFromAdjust(Course course) {
        long injectedTime = course.getTime();
        Course current = course;
        current.setExpireAt(current.getPrevious().getExpireAt().plusMillis(current.getTime()));
        while (current.hasNext()) {
            current = current.getNext();
            current.setExpireAt(current.getExpireAt().plusMillis(injectedTime));
        }
    }

    public void deleteCourse(Course course) {
        Course previous = course.getPrevious();
        Course next = course.getNext();
        if(course.hasPrevious()) {
            course.getPrevious().setNext(course.getNext());
            courseService.save(course.getPrevious());
        }
        if(course.hasNext()) {
            course.getNext().setPrevious(course.getPrevious());
            recalculateExpirationsFromDelete(course);
        }

        courseService.deleteById(course.getId());
    }

    private void recalculateExpirationsFromDelete(Course course) {
        long deletedTime = course.getTime();
        Course current = course;
        while (current.hasNext()) {
            current = current.getNext();
            current.setExpireAt(current.getExpireAt().minusMillis(deletedTime));
        }
    }
}
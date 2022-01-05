package io.solar.facade;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.entity.CourseType;
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
        if (dto.getNextId() == null) {
            extendCourseChain(dto);
        } else {
            adjustCourseChain(dto);
        }
    }

    private void extendCourseChain(CourseDto dto) {
        Course last = courseMapper.toEntity(dto);
        if (last.getCourseType().equals(CourseType.ATTACH_TO_ORBIT)) {
            last.setTime(0L);
        }
        Optional<Course> previousLast = courseService.findLastCourse(last.getObject());
        if (previousLast.isPresent()) {
            Course previous = previousLast.get();
            last.setPrevious(previous);
            last.setCreatedAt(Instant.now());
            courseService.save(last);
            previous.setNext(last);
            courseService.save(previous);
        } else {
            last.setCreatedAt(Instant.now());
            courseService.save(last);
        }
    }

    private void adjustCourseChain(CourseDto dto) {
        Course newCourse = courseMapper.toEntity(dto);

        if (newCourse.getCourseType().equals(CourseType.ATTACH_TO_ORBIT)) {
            newCourse.setTime(0L);
        }

        Optional<Course> previousOptional = courseService.findByNext(courseService.findById(dto.getNextId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("There is no course with such id in database. id = %d", dto.getNextId()))));

        Course next = newCourse.getNext();
        if (previousOptional.isPresent()) {
            Course previous = previousOptional.get();
            newCourse.setPrevious(previous);
            saveAt(newCourse);
            previous.setNext(newCourse);
            courseService.save(previous);
        } else {
            saveAt(newCourse);
        }
        next.setPrevious(newCourse);
        courseService.save(next);
    }

    private void saveAt(Course course) {
        course.setCreatedAt(Instant.now());
        courseService.save(course);
    }

    public void deleteCourse(Course course) {
        Course previous = course.getPrevious();
        Course next = course.getNext();
        if (course.hasPrevious()) {
            previous.setNext(next);
            courseService.save(previous);
        }
        if (course.hasNext()) {
            next.setPrevious(previous);
            courseService.save(next);
        }
        courseService.deleteById(course.getId());
    }
}
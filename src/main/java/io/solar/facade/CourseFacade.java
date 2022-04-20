package io.solar.facade;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.entity.CourseType;
import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.mapper.CourseMapper;
import io.solar.service.CourseService;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseFacade {

    private final CourseService courseService;
    private final CourseMapper courseMapper;
    private final StarShipService starShipService;
    private final SpaceTechEngine spaceTechEngine;

    public void updateCourseChain(CourseDto dto, User user) {
        Course course = courseMapper.toEntity(dto);
        checkAccelLimit(course, starShipService.getById(user.getLocation().getId()));
        if (dto.getNextId() == null) {
            extendCourseChain(course);
        } else {
            adjustCourseChain(course);
        }
    }

    private void checkAccelLimit(Course course, StarShip starship) {
        double maxAccel = spaceTechEngine.calculateMaxAcceleration(starship);
        double courseAccel = calculateAcceleration(course.getAccelerationX(), course.getAccelerationY());
        if (courseAccel > maxAccel) {
            double accelDelta = maxAccel/courseAccel;
            decreaseCourseAcceleration(course, accelDelta);
        }
    }

    private void decreaseCourseAcceleration(Course course, double accelDelta) {
        course.setAccelerationX(course.getAccelerationX() * accelDelta);
        course.setAccelerationY(course.getAccelerationY() * accelDelta);
    }

    private double calculateAcceleration(Double accelerationX, Double accelerationY) {

        return Math.sqrt(Math.pow(accelerationX, 2) + Math.pow(accelerationY, 2));
    }

    private void extendCourseChain(Course last) {

        if (CourseType.ATTACH_TO_ORBIT.equals(last.getCourseType())) {
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

    private void adjustCourseChain(Course newCourse) {

        if (CourseType.ATTACH_TO_ORBIT.equals(newCourse.getCourseType())) {
            newCourse.setTime(0L);
        }

        Course next = newCourse.getNext();

        Optional<Course> previousOptional = courseService.findByNext(next);

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
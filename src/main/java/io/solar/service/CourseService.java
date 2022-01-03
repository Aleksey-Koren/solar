package io.solar.service;


import io.solar.entity.Course;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> findByNext(Course next) {
        return courseRepository.findByNext(next);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    public Course findActiveCourse(BasicObject object) {

        return courseRepository.findTopByObjectOrderByExpireAtDesc(object);
    }

    public Optional<Course> findLastCourse(BasicObject object) {
        return courseRepository.findByObjectAndNextIsNull(object);
    }
}
package io.solar.repository;

import io.solar.entity.Course;
import io.solar.entity.objects.BasicObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByNext(Course next);

    Course findTopByObjectAndExpireAtAfterOrderByExpireAt(BasicObject object, Instant now);

    Optional<Course> findByObjectAndNextIsNull(BasicObject object);
}

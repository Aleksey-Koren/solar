package io.solar.mapper;

import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.CourseRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CourseMapper {

    private final CourseRepository courseRepository;
    private final BasicObjectRepository basicObjectRepository;

    public Course toEntity(CourseDto dto) {
        Course course;
        if(dto.getId() == null) {
            course = new Course();
        }else{
            course = courseRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no course with such id in database"));
        }

        return fillEntityFields(course, dto);
    }

    private Course fillEntityFields(Course course, CourseDto dto) {
        if(dto.getObjectId() == null) {
            throw new ServiceException("Course entity must belong to existing object. It can't exists without an owner-object");
        }
        course.setObject(basicObjectRepository.findById(dto.getObjectId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no object with such id in database")));
        course.setX(dto.getX());
        course.setY(dto.getY());
        course.setAccelerationX(dto.getAccelerationX());
        course.setAccelerationY(dto.getAccelerationY());
        course.setNext(dto.getNextId() != null ? courseRepository.findById(dto.getNextId())
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no course with such id in database"))
                                               : null);
        course.setCreatedAt(dto.getCreatedAt());
        course.setExpireAt(dto.getExpireAt());
        return course;
    }
}

package io.solar.mapper;

import io.solar.config.properties.AppProperties;
import io.solar.dto.CourseDto;
import io.solar.entity.Course;
import io.solar.entity.CourseType;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.CourseRepository;
import io.solar.repository.PlanetRepository;
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
    private final PlanetRepository planetRepository;
    private final AppProperties appProperties;

    public Course toEntity(CourseDto dto) {
        Course course;
        if(dto.getId() == null) {
            course = new Course();
        }else{
            course = courseRepository.findById(dto.getNextId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            String.format("There is no course with id = %d in database", dto.getId())));
        }

        return fillEntityFields(course, dto);
    }

    private Course fillEntityFields(Course course, CourseDto dto) {
        if(dto.getObjectId() == null) {
            throw new ServiceException("Course entity must belong to existing object. It can't exists without an owner-object");
        }
        course.setObject(basicObjectRepository.findById(dto.getObjectId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        String.format("There is no object with id = %d in database", dto.getObjectId()))));
        course.setAccelerationX(dto.getAccelerationX());
        course.setAccelerationY(dto.getAccelerationY());
        course.setTime((dto.getTime() * 1000) / (long) Math.pow(appProperties.getTimeFlowModifier(), 2));
        course.setNext(dto.getNextId() != null ? courseRepository.findById(dto.getNextId())
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                            String.format("There is no course with id = %d in database", dto.getNextId())))
                                               : null);
        course.setCreatedAt(dto.getCreatedAt());
        course.setExpireAt(dto.getExpireAt());
        course.setPlanet(dto.getPlanetId() != null ? planetRepository.findById(dto.getPlanetId())
                                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                            String.format("There is no planet with id = %d in database", dto.getPlanetId())))
                                                : null);

        course.setCourseType(dto.getCourseType() != null ? CourseType.valueOf(dto.getCourseType()) : null);
        return course;
    }
}
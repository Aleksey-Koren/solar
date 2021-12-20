package io.solar.dto;

import io.solar.entity.Course;
import lombok.Data;

import java.time.Instant;

@Data
public class CourseDto {

    private Long id;
    private Long objectId;
    private Float x;
    private Float y;
    private Float accelerationX;
    private Float accelerationY;
    private Long nextId;
    private Instant createdAt;
    private Instant expireAt;
}

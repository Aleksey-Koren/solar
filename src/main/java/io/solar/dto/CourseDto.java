package io.solar.dto;

import io.solar.entity.Course;
import lombok.Data;

import java.time.Instant;

@Data
public class CourseDto {

    private Long id;
    private Long objectId;
    //@todo - delete
    private Float x;
    //@todo - delete
    private Float y;

    //@todo new field - duration
    private Long time;

    private Float accelerationX;
    private Float accelerationY;
    private Long nextId;
    private Instant createdAt;
    private Instant expireAt;
}

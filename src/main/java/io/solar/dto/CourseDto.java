package io.solar.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CourseDto {

    private Long id;
    private Long objectId;
    private Long time;
    private Float accelerationX;
    private Float accelerationY;
    private Long nextId;
    private Instant createdAt;
    private Instant expireAt;
    private Long planetId;
    private String courseType;
}
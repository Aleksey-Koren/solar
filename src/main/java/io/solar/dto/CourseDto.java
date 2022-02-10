package io.solar.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CourseDto {

    private Long id;
    private Long objectId;
    //in milliseconds (should be float in seconds, because milliseconds may have too large fraction)
    private Long time;
    private Double accelerationX;
    private Double accelerationY;
    private Long nextId;
    private Instant createdAt;
    private Long expireAt;
    private Long planetId;
    private String courseType;
}
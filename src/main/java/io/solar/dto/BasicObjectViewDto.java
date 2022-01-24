package io.solar.dto;

import io.solar.entity.objects.ObjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BasicObjectViewDto {

    private Long id;
    private Long planet;
    private Long population;
    private String fraction;
    private String title;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;
    private Float rotationAngle;
    private Long hullId;
    private Long userId;
    private Boolean active;
    private Long durability;
    private Long attachedToShip;
    private Long attachedToSocket;
    private ObjectStatus status;
    private Float speedX;
    private Float speedY;
    private Float accelerationX;
    private Float accelerationY;
    private Long positionIterationTs;
    private Boolean clockwiseRotation;
    private Float volume;
}
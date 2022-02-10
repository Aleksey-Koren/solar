package io.solar.dto.object;

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
    private Double x;
    private Double y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;
    private Float rotationAngle;
    private Long hullId;
    private Boolean active;
    private Integer durability;
    private Long attachedToShip;
    private Long attachedToSocket;
    private ObjectStatus status;
    private Double speedX;
    private Double speedY;
    private Double accelerationX;
    private Double accelerationY;
    private Long positionIterationTs;
    private Boolean clockwiseRotation;
    private Float volume;
}
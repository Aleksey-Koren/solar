package io.solar.dto;

import io.solar.entity.objects.ObjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private Long hullId;
    private Long userId;
    private Boolean active;
    private Long durability;
    private Long attachedToShip;
    private Long attachedToSocket;
    private ObjectStatus status;
    private Float acceleration;
    private Float speed;
}

package io.solar.dto;

import io.solar.entity.objects.ObjectStatus;
import lombok.Data;

@Data
public class ItemObjectDto {

    private Long id;
    private Long planetId;
    private String fraction;
    private String title;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;
    private Long objectTypeDescriptionId;
    private Long userId;
    private Boolean active;
    private Long durability;
    private Long attachedToShip;
    private Long attachedToSocket;
    private ObjectStatus objectStatus;
    private Float acceleration;
    private Float speed;
}
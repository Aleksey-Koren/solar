package io.solar.entity.objects;

import io.solar.entity.inventory.InventorySocket;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class AbstractObject {

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

    private List<InventorySocket> socketList;
    private List<ObjectItem> attachedObjects;
}
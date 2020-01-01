package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Description for inventory item
 * NOT A INSTANCE
 */
@Getter
@Setter
public class InventoryItem {
    private Long id;
    private Long inventoryType;
    private String title;
    private Float powerMin;
    private Float powerMax;
    private Float powerDegradation;
    private Long distance;
    private Float cooldown;
    private Long energyConsumption;
    private Long durability;
    private Long mass;
    private String description;
    private Long price;

    List<InventoryModification> modifications;
    List<InventoryType> sockets;
}

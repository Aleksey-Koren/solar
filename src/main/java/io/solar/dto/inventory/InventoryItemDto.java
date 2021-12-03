package io.solar.dto.inventory;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Description for inventory item
 * NOT A INSTANCE
 */

@Data
@Builder
public class InventoryItemDto {
    private Long id;
    private Integer inventoryType;
    private String title;
    private Float powerMin;
    private Float powerMax;
    private Float powerDegradation;
    private Float distance;
    private Float cooldown;
    private Integer energyConsumption;
    private Integer durability;
    private Integer mass;
    private String description;
    private Integer price;

    List<InventoryModificationDto> modifications;
    List<InventorySocketDto> sockets;
}

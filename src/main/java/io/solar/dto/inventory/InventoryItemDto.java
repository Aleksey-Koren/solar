package io.solar.dto.inventory;

import io.solar.dto.ObjectModificationTypeDto;
import io.solar.entity.objects.ObjectSubType;
import io.solar.entity.objects.ObjectType;
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
    private ObjectType type;
    private ObjectSubType subType;

    List<ObjectModificationTypeDto> modifications;
    List<InventorySocketDto> sockets;
}

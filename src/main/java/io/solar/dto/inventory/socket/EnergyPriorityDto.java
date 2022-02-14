package io.solar.dto.inventory.socket;

import lombok.Data;

@Data
public class EnergyPriorityDto {
    private Long inventorySocketId;
    private Integer energyConsumptionPriority;
}

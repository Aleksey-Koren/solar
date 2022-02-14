package io.solar.dto.inventory.socket;

import lombok.Data;

@Data
public class EnergyPriorityDto {
    private Long socketId;
    private Integer energyConsumptionPriority;
}

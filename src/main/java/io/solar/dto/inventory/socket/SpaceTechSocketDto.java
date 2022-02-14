package io.solar.dto.inventory.socket;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpaceTechSocketDto {
    private Long id;
    private Long spaceTechId;
    private Long inventorySocketId;
    private Integer energyConsumptionPriority;
}

package io.solar.dto.object;

import io.solar.dto.GoodsDto;
import io.solar.dto.ProductionDto;
import io.solar.entity.Inventory;
import io.solar.dto.inventory.InventorySocketDto;
import lombok.Data;

import java.util.List;

@Data
public class StationDto extends BasicObjectViewDto {

    private Long userId;
    private Long money;

    private List<ProductionDto> production;
    private List<GoodsDto> goods;
    private List<Inventory> inventory;

    private List<BasicObjectViewDto> attachedObjects;
    private List<InventorySocketDto> socketList;
}
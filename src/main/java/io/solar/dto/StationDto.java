package io.solar.dto;

import io.solar.entity.Goods;
import io.solar.entity.Inventory;
import io.solar.entity.inventory.InventorySocket;
import lombok.Data;

import java.util.List;

@Data
public class StationDto extends BasicObjectViewDto{

    private List<ProductionDto> production;
    private List<Goods> goods;
    private List<Inventory> inventory;

    private List<BasicObjectViewDto> attachedObjects;
    private List<InventorySocket> socketList;
}

package io.solar.dto;

import io.solar.entity.Goods;
import io.solar.entity.Inventory;
import io.solar.dto.inventory.InventorySocketDto;
import lombok.Data;

import java.util.List;

@Data
public class StationDto extends BasicObjectViewDto{

    //TODO We have to decide witch collections(dtos or entities) we should send to front when we get 1 station by id
    private List<ProductionDto> production;
    private List<Goods> goods;
    private List<Inventory> inventory;

    private List<BasicObjectViewDto> attachedObjects;
    private List<InventorySocketDto> socketList;
}

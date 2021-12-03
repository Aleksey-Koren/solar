package io.solar.mapper;


import io.solar.dto.InventoryTypeDto;
import io.solar.entity.inventory.InventoryType;
import io.solar.service.InventoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InventoryTypeMapper {

    private InventoryTypeService inventoryTypeService;

    @Autowired
    public InventoryTypeMapper(InventoryTypeService inventoryTypeService) {
        this.inventoryTypeService = inventoryTypeService;
    }

    public InventoryType toEntity(InventoryTypeDto dto) {
        InventoryType inventoryType;
        if (dto.getId() != null) {
            inventoryType = inventoryTypeService.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such InventoryType ID in database"));
        }else{
            inventoryType = new InventoryType();
        }

        inventoryType.setTitle(dto.getTitle());

        return inventoryType;
    }

    public InventoryTypeDto toDto(InventoryType inventoryType) {
        InventoryTypeDto dto = new InventoryTypeDto();
        dto.setId(inventoryType.getId());
        dto.setTitle(inventoryType.getTitle());
        return dto;
    }
}

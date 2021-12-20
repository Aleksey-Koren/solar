package io.solar.mapper;


import io.solar.dto.inventory.InventoryTypeDto;
import io.solar.entity.inventory.InventoryType;
import io.solar.service.inventory.InventoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class InventoryTypeMapper {

    private final InventoryTypeService inventoryTypeService;

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

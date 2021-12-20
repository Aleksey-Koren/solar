package io.solar.facade;

import io.solar.dto.inventory.InventoryTypeDto;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.service.inventory.InventoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryTypeFacade {

    private final InventoryTypeService inventoryTypeService;
    private final InventoryTypeMapper inventoryTypeMapper;

    public InventoryTypeDto save(InventoryTypeDto dto) {
        return inventoryTypeMapper.toDto(inventoryTypeService.save(inventoryTypeMapper.toEntity(dto)));
    }

    public Page<InventoryTypeDto> findAll(Pageable pageable) {
        return inventoryTypeService.findAll(pageable).map(inventoryTypeMapper::toDto);
    }
}

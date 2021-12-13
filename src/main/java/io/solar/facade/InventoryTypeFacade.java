package io.solar.facade;

import io.solar.dto.inventory.InventoryTypeDto;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.service.inventory.InventoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InventoryTypeFacade {

    private InventoryTypeService service;
    private InventoryTypeMapper mapper;

    @Autowired
    public InventoryTypeFacade(InventoryTypeService inventoryTypeService, InventoryTypeMapper inventoryTypeMapper) {
        this.service = inventoryTypeService;
        this.mapper = inventoryTypeMapper;
    }

    public InventoryTypeDto save(InventoryTypeDto dto) {
        return mapper.toDto(service.save(mapper.toEntity(dto)));
    }

    public Page<InventoryTypeDto> findAll(Pageable pageable) {
        return service.findAll(pageable).map(mapper::toDto);
    }
}

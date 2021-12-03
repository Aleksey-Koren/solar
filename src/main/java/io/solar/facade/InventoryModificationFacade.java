package io.solar.facade;

import io.solar.dto.inventory.InventoryModificationDto;
import io.solar.entity.inventory.InventoryModification;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.service.InventoryModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryModificationFacade {

    private final InventoryModificationService inventoryModificationService;
    private final InventoryModificationMapper inventoryModificationMapper;

    @Autowired
    public InventoryModificationFacade(InventoryModificationService inventoryModificationService,
                                       InventoryModificationMapper inventoryModificationMapper) {

        this.inventoryModificationService = inventoryModificationService;
        this.inventoryModificationMapper = inventoryModificationMapper;
    }

    public List<InventoryModificationDto> getAll() {

        return inventoryModificationService.getAll()
                .stream()
                .map(inventoryModificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public InventoryModificationDto save(InventoryModificationDto inventoryModificationDto) {
        InventoryModification inventoryModification = inventoryModificationMapper.toEntity(inventoryModificationDto);

        return inventoryModificationMapper.toDto(
                inventoryModificationService.save(inventoryModification)
        );
    }
}

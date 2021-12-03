package io.solar.facade;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.mapper.InventoryItemMapper;
import io.solar.service.ObjectTypeDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectTypeDescriptionFacade {

    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final InventoryItemMapper inventoryItemMapper;

    @Autowired
    public ObjectTypeDescriptionFacade(ObjectTypeDescriptionService objectTypeDescriptionService,
                                       InventoryItemMapper inventoryItemMapper) {

        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.inventoryItemMapper = inventoryItemMapper;
    }

    public List<InventoryItemDto> getAll() {

        return objectTypeDescriptionService.getAll()
                .stream()
                .map(inventoryItemMapper::toDto)
                .collect(Collectors.toList());
    }

}

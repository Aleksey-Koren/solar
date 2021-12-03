package io.solar.facade;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.dto.inventory.InventoryModificationDto;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.InventoryItemMapper;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.service.BasicObjectService;
import io.solar.service.InventorySocketService;
import io.solar.service.ObjectModificationService;
import io.solar.service.ObjectTypeDescriptionService;
import io.solar.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObjectTypeDescriptionFacade {

    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final BasicObjectService basicObjectService;
    private final ProductionService productionService;
    private final InventorySocketService inventorySocketService;
    private final ObjectModificationService objectModificationService;
    private final InventoryItemMapper inventoryItemMapper;
    private final InventoryModificationMapper inventoryModificationMapper;

    @Autowired
    public ObjectTypeDescriptionFacade(ObjectTypeDescriptionService objectTypeDescriptionService,
                                       BasicObjectService basicObjectService,
                                       ProductionService productionService,
                                       InventorySocketService inventorySocketService,
                                       ObjectModificationService objectModificationService,
                                       InventoryItemMapper inventoryItemMapper,
                                       InventoryModificationMapper inventoryModificationMapper) {

        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.basicObjectService = basicObjectService;
        this.productionService = productionService;
        this.inventorySocketService = inventorySocketService;
        this.objectModificationService = objectModificationService;
        this.inventoryItemMapper = inventoryItemMapper;
        this.inventoryModificationMapper = inventoryModificationMapper;
    }

    public List<InventoryItemDto> getAll() {

        return objectTypeDescriptionService.getAll()
                .stream()
                .map(inventoryItemMapper::toDto)
                .collect(Collectors.toList());
    }

    public InventoryItemDto save(InventoryItemDto inventoryItemDto) {
        ObjectTypeDescription objectTypeDescription = inventoryItemMapper.toEntity(inventoryItemDto);

        return inventoryItemMapper.toDto(
                objectTypeDescriptionService.save(objectTypeDescription)
        );
    }

    public List<InventoryModificationDto> findAllModifications(Long itemId) {

        return objectModificationService.findAllModifications(itemId)
                .stream()
                .map(modification -> inventoryModificationMapper.toDto(modification.getModification()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        objectModificationService.deleteByItemId(id);
        inventorySocketService.deleteByItemId(id);
        productionService.deleteAllByObjectDescriptionId(id);
        basicObjectService.deleteByHullId(id);
        objectTypeDescriptionService.delete(id);
    }

}

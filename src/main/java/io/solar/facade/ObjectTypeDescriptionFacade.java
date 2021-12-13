package io.solar.facade;

import io.solar.dto.ObjectModificationTypeDto;
import io.solar.dto.inventory.InventoryItemDto;
import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.objects.ObjectModification;
import io.solar.entity.objects.ObjectModificationType;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.ObjectTypeDescriptionMapper;
import io.solar.mapper.ObjectModificationTypeMapper;
import io.solar.mapper.SocketMapper;
import io.solar.service.object.BasicObjectService;
import io.solar.service.inventory.InventorySocketService;
import io.solar.service.object.ObjectModificationService;
import io.solar.service.object.ObjectTypeDescriptionService;
import io.solar.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
    private final ObjectTypeDescriptionMapper objectTypeDescriptionMapper;
    private final ObjectModificationTypeMapper objectModificationTypeMapper;
    private final SocketMapper socketMapper;

    @Autowired
    public ObjectTypeDescriptionFacade(ObjectTypeDescriptionService objectTypeDescriptionService,
                                       BasicObjectService basicObjectService,
                                       ProductionService productionService,
                                       InventorySocketService inventorySocketService,
                                       ObjectModificationService objectModificationService,
                                       ObjectTypeDescriptionMapper objectTypeDescriptionMapper,
                                       ObjectModificationTypeMapper objectModificationTypeMapper,
                                       SocketMapper socketMapper) {

        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.basicObjectService = basicObjectService;
        this.productionService = productionService;
        this.inventorySocketService = inventorySocketService;
        this.objectModificationService = objectModificationService;
        this.objectTypeDescriptionMapper = objectTypeDescriptionMapper;
        this.objectModificationTypeMapper = objectModificationTypeMapper;
        this.socketMapper = socketMapper;
    }

    public List<InventoryItemDto> getAll() {

        return objectTypeDescriptionService.getAll()
                .stream()
                .map(objectTypeDescriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    public InventoryItemDto save(InventoryItemDto inventoryItemDto) {
        ObjectTypeDescription objectTypeDescription = objectTypeDescriptionMapper.toEntity(inventoryItemDto);

        return objectTypeDescriptionMapper.toDto(
                objectTypeDescriptionService.save(objectTypeDescription)
        );
    }

    @Transactional
    public void saveModifications(InventoryItemDto inventoryItemDto) {
        List<ObjectModificationTypeDto> modifications = inventoryItemDto.getModifications();

        if (CollectionUtils.isEmpty(modifications)) {
            objectModificationService.deleteByItemId(inventoryItemDto.getId());
            return;
        }

        List<ObjectModificationType> existModifications = objectModificationService.findAllObjectModifications(inventoryItemDto.getId())
                .stream()
                .map(ObjectModification::getModification)
                .collect(Collectors.toList());


        List<Long> toDeleteIds = existModifications.stream()
                .filter(modification -> !modifications.contains(objectModificationTypeMapper.toDto(modification)))
                .map(ObjectModificationType::getId)
                .collect(Collectors.toList());


        List<ObjectModification> toInsert = modifications.stream()
                .map(objectModificationTypeMapper::toEntity)
                .filter(modification -> !existModifications.contains(modification))
                .map(modification -> new ObjectModification(null, objectTypeDescriptionMapper.toEntity(inventoryItemDto), modification))
                .collect(Collectors.toList());

        objectModificationService.saveAll(toInsert);
        objectModificationService.deleteModificationsWithItemId(toDeleteIds, inventoryItemDto.getId());
    }

    @Transactional
    public void saveSockets(InventoryItemDto inventoryItem) {
        List<InventorySocketDto> sockets = inventoryItem.getSockets();

        if (CollectionUtils.isEmpty(sockets)) {
            inventorySocketService.deleteByItemId(inventoryItem.getId());
            return;
        }

        List<InventorySocket> toSave = sockets.stream()
                .peek(socket -> {
                    socket.setSortOrder(sockets.indexOf(socket) + 1);
                    socket.setItemId(inventoryItem.getId());
                })
                .map(socketMapper::toEntity)
                .collect(Collectors.toList());

        List<InventorySocket> toDelete = inventorySocketService.findAllSockets(inventoryItem.getId())
                .stream()
                .filter(socket -> !sockets.contains(socketMapper.toDto(socket)))
                .collect(Collectors.toList());

        inventorySocketService.saveAll(toSave);
        inventorySocketService.deleteAll(toDelete);
    }

    @Transactional
    public void delete(Long id) {
        objectModificationService.deleteByItemId(id);
        inventorySocketService.deleteByItemId(id);
        productionService.deleteAllByObjectDescriptionId(id);
        basicObjectService.deleteByHullId(id);
        objectTypeDescriptionService.delete(id);
    }

    public List<ObjectModificationTypeDto> findAllModifications(Long itemId) {

        return objectModificationService.findAllObjectModifications(itemId)
                .stream()
                .map(modification -> objectModificationTypeMapper.toDto(modification.getModification()))
                .collect(Collectors.toList());
    }

    public List<InventorySocketDto> findAllSockets(Long itemId) {

        return inventorySocketService.findAllSockets(itemId)
                .stream()
                .map(socketMapper::toDto)
                .collect(Collectors.toList());
    }
}

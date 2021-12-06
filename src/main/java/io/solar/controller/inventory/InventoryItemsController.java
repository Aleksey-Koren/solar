package io.solar.controller.inventory;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.facade.ObjectTypeDescriptionFacade;
import io.solar.mapper.ObjectTypeDescriptionMapper;
import io.solar.service.ObjectTypeDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/inventory-item")
public class InventoryItemsController {

    private final ObjectTypeDescriptionFacade objectTypeDescriptionFacade;
    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final ObjectTypeDescriptionMapper objectTypeDescriptionMapper;

    @Autowired
    public InventoryItemsController(ObjectTypeDescriptionMapper objectTypeDescriptionMapper,
                                    ObjectTypeDescriptionService objectTypeDescriptionService,
                                    ObjectTypeDescriptionFacade objectTypeDescriptionFacade) {

        this.objectTypeDescriptionMapper = objectTypeDescriptionMapper;
        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.objectTypeDescriptionFacade = objectTypeDescriptionFacade;
    }

    @GetMapping
    public List<InventoryItemDto> getAll() {

        return objectTypeDescriptionFacade.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public ResponseEntity<InventoryItemDto> save(@RequestBody InventoryItemDto inventoryItem) {
        InventoryItemDto savedInventoryItem = objectTypeDescriptionFacade.save(inventoryItem);

        objectTypeDescriptionFacade.saveModifications(inventoryItem);
        objectTypeDescriptionFacade.saveSockets(inventoryItem);

        return ResponseEntity.ok(savedInventoryItem);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public void delete(@PathVariable("id") Long id) {

        objectTypeDescriptionFacade.delete(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<InventoryItemDto> getOne(@PathVariable("id") Long itemId) {
        Optional<ObjectTypeDescription> objectOptional = objectTypeDescriptionService.findById(itemId);

        if (objectOptional.isPresent()) {
            InventoryItemDto inventoryItemDto = objectTypeDescriptionMapper.toDto(objectOptional.get());
            inventoryItemDto.setModifications(objectTypeDescriptionFacade.findAllModifications(inventoryItemDto.getId()));
            inventoryItemDto.setSockets(objectTypeDescriptionFacade.findAllSockets(inventoryItemDto.getId()));

            return ResponseEntity.ok(inventoryItemDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

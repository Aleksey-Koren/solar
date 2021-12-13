package io.solar.controller.inventory;

import io.solar.dto.ObjectModificationTypeDto;
import io.solar.facade.InventoryModificationFacade;
import io.solar.service.inventory.InventoryModificationService;
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

@RestController
@RequestMapping(value = "api/inventory-modification")
public class InventoryModificationsController {

    private final InventoryModificationFacade inventoryModificationFacade;
    private final InventoryModificationService inventoryModificationService;

    @Autowired
    public InventoryModificationsController(InventoryModificationFacade inventoryModificationFacade,
                                            InventoryModificationService inventoryModificationService) {

        this.inventoryModificationFacade = inventoryModificationFacade;
        this.inventoryModificationService = inventoryModificationService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public ResponseEntity<ObjectModificationTypeDto> save(@RequestBody ObjectModificationTypeDto inventoryTweek) {

        return ResponseEntity.ok(inventoryModificationFacade.save(inventoryTweek));
    }

    @GetMapping
    public List<ObjectModificationTypeDto> getAll() {

        return inventoryModificationFacade.getAll();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public void delete(@PathVariable("id") Long id) {

        inventoryModificationService.delete(id);
    }

}
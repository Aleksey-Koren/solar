package io.solar.controller.inventory;

import io.solar.dto.inventory.InventoryTypeDto;
import io.solar.facade.InventoryTypeFacade;
import io.solar.service.inventory.InventoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/inventory-type")
@RequiredArgsConstructor
public class InventoryTypeController {

    private final InventoryTypeFacade inventoryTypeFacade;
    private final InventoryTypeService inventoryTypeService;


    @Transactional
    @PreAuthorize("hasAuthority('EDIT_INVENTORY_TYPE')")
    @PostMapping
    public ResponseEntity<InventoryTypeDto> save(@RequestBody InventoryTypeDto dto) {
        return ResponseEntity.ok(inventoryTypeFacade.save(dto));
    }

    //TODO I didn't see any fields for filtration or searching on UI.
    // We should decide if we do filtration at this endpoint.
    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_INVENTORY_TYPE')")
    @GetMapping
    public ResponseEntity<Page<InventoryTypeDto>> getAll(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok().body(inventoryTypeFacade.findAll(pageable));
    }

    //TODO This method won't work, while I don't set all ManyToMany or OneToMany relations in entities.
    // Constraints in database reject deleting.
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_INVENTORY_TYPE')")
    @DeleteMapping ("{id}")
    public void delete(@PathVariable("id") Long id) {
        inventoryTypeService.delete(id);
    }
}

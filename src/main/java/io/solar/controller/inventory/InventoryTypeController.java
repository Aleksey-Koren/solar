package io.solar.controller.inventory;

import io.solar.dto.InventoryTypeDto;
import io.solar.facade.InventoryTypeFacade;
import io.solar.service.InventoryTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/inventory-type")
public class InventoryTypeController {

    private InventoryTypeFacade facade;
    private InventoryTypeService service;

    @Autowired
    public InventoryTypeController(InventoryTypeFacade inventoryTypeFacade, InventoryTypeService inventoryTypeService) {
        this.facade = inventoryTypeFacade;
        this.service = inventoryTypeService;
    }

    @Transactional
    @PreAuthorize("hasAuthority('EDIT_INVENTORY_TYPE')")
    @PostMapping
    public ResponseEntity<InventoryTypeDto> save(@RequestBody InventoryTypeDto dto) {
        return ResponseEntity.ok(facade.save(dto));
    }

    //TODO I didn't see any fields for filtration or searching on UI.
    // We should decide if we do filtration at this endpoint.
//    @Transactional
//    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_INVENTORY_TYPE')")
//    @GetMapping
//    public ResponseEntity<Page<InventoryTypeDto>> getAll(@PageableDefault Pageable pageable) {
//        return ResponseEntity.ok().body(facade.findAll(pageable));
//    }

    //TODO Frontend doesn't work with Page<InventoryTypeDto>. It works only with List<InventoryTypeDto>
    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_INVENTORY_TYPE')")
    @GetMapping
    public ResponseEntity<List<InventoryTypeDto>> getAll(@PageableDefault(page = 0, size = 50) Pageable pageable) {
        return ResponseEntity.ok(facade.findAll(pageable).getContent());
    }



    //TODO This method won't work, while I don't set all ManyToMany or OneToMany relations in entities.
    // Constraints in database reject deleting.
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_INVENTORY_TYPE')")
    @DeleteMapping ("{id}")
    public void delete(@PathVariable("id") Long id) {
        service.delete(id);
    }
}

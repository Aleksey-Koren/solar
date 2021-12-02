package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.dto.InventoryModificationDto;
import io.solar.entity.User;
import io.solar.entity.inventory.InventoryModification;
import io.solar.facade.InventoryModificationFacade;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/inventory-modification")
public class InventoryModificationsController {

    private final InventoryModificationFacade inventoryModificationFacade;

    @Autowired
    public InventoryModificationsController(InventoryModificationFacade inventoryModificationFacade) {
        this.inventoryModificationFacade = inventoryModificationFacade;
    }


    @PostMapping
    public InventoryModification save(@RequestBody InventoryModification inventoryTweek, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        Query query;
        if(inventoryTweek.getId() != null) {
            query = transaction.query("update object_modification_type set title = :title, data = :data, description = :description where id = :id ");
            query.setLong("id", inventoryTweek.getId());
        } else {
            query = transaction.query("insert into object_modification_type (" +
                    "title, data, description) values (:title, :data, :description)");
        }
        query.setString("title", inventoryTweek.getTitle());
        query.setString("data", inventoryTweek.getData());
        query.setString("description", inventoryTweek.getDescription());

        query.execute();
        if(inventoryTweek.getId() == null) {
            inventoryTweek.setId(query.getLastGeneratedKey(Long.class));
        }
        return inventoryTweek;
    }


    @GetMapping
    public List<InventoryModificationDto> getAll() {

        return inventoryModificationFacade.getAll();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSIONS')") //todo: change authority role (add in db 'edit-authority')
    public void delete(@PathVariable("id") Long id) {
        inventoryModificationFacade.delete(id);
    }

}

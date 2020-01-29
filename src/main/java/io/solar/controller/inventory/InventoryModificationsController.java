package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.inventory.InventoryModification;
import io.solar.entity.User;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "inventory-modification")
public class InventoryModificationsController {

    @RequestMapping(method = "post")
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


    @RequestMapping
    public List<InventoryModification> getAll(Transaction transaction) {
        return transaction.query("select * from object_modification_type").executeQuery(new InventoryModificationMapper());
    }

    @RequestMapping(method = "delete", value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        Query query = transaction.query("delete from object_modification_type where id = :id");
        query.setLong("id", id);
        query.execute();
    }

}

package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.InventoryModification;
import io.solar.entity.User;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "inventory-modification")
public class InventoryModificationsController {

    @RequestMapping(method = "post")
    public InventoryModification save(@RequestBody InventoryModification inventoryTweek, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory")) {
            throw new RuntimeException("no privileges");
        }
        Query query;
        if(inventoryTweek.getId() != null) {
            query = transaction.query("update inventory_modification set title = :title, data = :data where id = :id ");
            query.setLong("id", inventoryTweek.getId());
        } else {
            query = transaction.query("insert into inventory_modification (" +
                    "title, data) values (:title, :data)");
        }
        query.setString("title", inventoryTweek.getTitle());
        query.setString("data", inventoryTweek.getData());

        query.execute();
        if(inventoryTweek.getId() == null) {
            inventoryTweek.setId(query.getLastGeneratedKey(Long.class));
        }
        return inventoryTweek;
    }


    @RequestMapping
    public List<InventoryModification> getAll(Transaction transaction) {
        return transaction.query("select * from inventory_modification").executeQuery(new InventoryModificationMapper());
    }

    @RequestMapping(method = "delete", value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory")) {
            throw new RuntimeException("no privileges");
        }
        Query query = transaction.query("delete from inventory_modification where id = :id");
        query.setLong("id", id);
        query.execute();
    }

}

package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.User;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "inventory-type")
public class InventoryTypeController {

    @RequestMapping(method = "post")
    public InventoryType save(@RequestBody InventoryType inventoryType, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory-type", transaction)) {
            throw new RuntimeException("no privileges");
        }

        Query query = transaction.query("select * from object_type where title = :title");
        query.setString("title", inventoryType.getTitle());
        List<InventoryType> check = query.executeQuery(new InventoryTypeMapper());
        if (check.size() > 0) {
            return check.get(0);
        }
        if (inventoryType.getId() != null) {
            query = transaction.query("update object_type set title = :title where id = :id");
            query.setLong("id", inventoryType.getId());
        } else {
            query = transaction.query("insert into object_type (title) values (:title)");
        }
        query.setString("title", inventoryType.getTitle());
        query.execute();
        if (inventoryType.getId() == null) {
            inventoryType.setId(query.getLastGeneratedKey(Long.class));
        }
        return inventoryType;
    }


    @RequestMapping
    public List<InventoryType> getAll(Transaction transaction) {
        Query query = transaction.query("select * from object_type");
        return query.executeQuery(new InventoryTypeMapper());
    }

    @RequestMapping(method = "delete", value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory-type", transaction)) {
            throw new RuntimeException("no privileges");
        }
        Query deleteObjects = transaction.query("delete from objects where hull_id in (" +
                "select id from object_type_description where object_type_description.inventory_type = :id)");
        deleteObjects.setLong("id", id);
        deleteObjects.execute();

        Query deleteObjectModifications = transaction.query("delete from object_modification" +
                " where item_id in (select id from object_type_description where inventory_type = :id)");
        deleteObjectModifications.setLong("id", id);
        deleteObjectModifications.execute();

        Query deleteObjectsDescriptions = transaction.query("delete from object_type_description where inventory_type = :id");
        deleteObjectsDescriptions.setLong("id", id);
        deleteObjectsDescriptions.execute();

        Query deleteSockets = transaction.query("delete from object_type_socket where item_type_id = :id");
        deleteSockets.setLong("id", id);
        deleteSockets.execute();

        Query deleteObjectType = transaction.query("delete from object_type where id = :id");
        deleteObjectType.setLong("id", id);
        deleteObjectType.execute();
    }


}

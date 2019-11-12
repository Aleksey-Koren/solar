package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.InventoryType;
import io.solar.entity.User;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "inventory-type")
public class InventoryTypeController {

    @RequestMapping(method = "post")
    public InventoryType save(@RequestBody InventoryType inventoryType, @AuthData User user) {
        if (!AuthController.userCan(user, "edit-inventory-type")) {
            throw new RuntimeException("no privileges");
        }
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from inventory_type where title = :title");
            query.setString("title", inventoryType.getTitle());
            List<InventoryType> check = query.executeQuery(new InventoryTypeMapper());
            if (check.size() > 0) {
                transaction.commit();
                return check.get(0);
            }
            if (inventoryType.getId() != null) {
                query = transaction.query("update inventory_type set title = :title where id = :id");
                query.setLong("id", inventoryType.getId());
            } else {
                query = transaction.query("insert into inventory_type (title) values (:title)");
            }
            query.setString("title", inventoryType.getTitle());
            query.execute();
            if (inventoryType.getId() == null) {
                inventoryType.setId(query.getLastGeneratedKey(Long.class));
            }
            transaction.commit();
            return inventoryType;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


    @RequestMapping
    public List<InventoryType> getAll() {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from inventory_type");
            List<InventoryType> users = query.executeQuery(new InventoryTypeMapper());
            transaction.commit();
            return users;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(method = "delete", value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user) {
        if (!AuthController.userCan(user, "edit-inventory-type")) {
            throw new RuntimeException("no privileges");
        }
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("delete from inventory_type where id = :id");
            query.setLong("id", id);
            query.execute();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


}

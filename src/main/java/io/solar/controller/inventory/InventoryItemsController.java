package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.inventory.InventoryItem;
import io.solar.entity.inventory.InventoryModification;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.User;
import io.solar.entity.util.ManyToMany;
import io.solar.mapper.InventoryItemMapper;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.mapper.SocketMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.SafeResultSet;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Controller
@RequestMapping(value = "inventory-item")
@Slf4j
public class InventoryItemsController {



    @RequestMapping(method = "post")
    public InventoryItem save(@RequestBody InventoryItem inventoryItem, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        Query query;
        if (inventoryItem.getId() != null) {
            query = transaction.query("update object_type_description set inventory_type = :inventoryType," +
                    " title = :title, power_min = :powerMin," +
                    " power_max = :powerMax, power_degradation = :powerDegradation," +
                    " cooldown = :cooldown, distance = :distance, energy_consumption = :energyConsumption," +
                    " durability = :durability, mass = :mass, description = :description, price = :price where id = :id ");
            query.setLong("id", inventoryItem.getId());
        } else {
            query = transaction.query("insert into object_type_description (" +
                    "inventory_type, title, power_min, power_max, power_degradation, cooldown, distance, energy_consumption, durability, mass, description, price" +
                    ") values (" +
                    ":inventoryType, :title, :powerMin, :powerMax, :powerDegradation, :cooldown, :distance, :energyConsumption, :durability, :mass, :description, :price" +
                    ")");
        }
        query.setLong("inventoryType", inventoryItem.getInventoryType());
        query.setString("title", inventoryItem.getTitle());
        query.setFloat("powerMin", inventoryItem.getPowerMin());
        query.setFloat("powerMax", inventoryItem.getPowerMax());
        query.setFloat("powerDegradation", inventoryItem.getPowerDegradation());
        query.setFloat("cooldown", inventoryItem.getCooldown());
        query.setLong("distance", inventoryItem.getDistance());
        query.setLong("energyConsumption", inventoryItem.getEnergyConsumption());
        query.setLong("durability", inventoryItem.getDurability());
        query.setLong("mass", inventoryItem.getMass());
        query.setString("description", inventoryItem.getDescription());
        query.setLong("price", inventoryItem.getPrice());

        query.execute();


        if (inventoryItem.getId() == null) {
            inventoryItem.setId(query.getLastGeneratedKey(Long.class));
        }

        saveModifications(inventoryItem, transaction);
        saveSockets(inventoryItem, transaction);

        return inventoryItem;
    }


    @RequestMapping
    public List<InventoryItem> getAll(Transaction transaction) {
        return transaction.query("select * from object_type_description").executeQuery(new InventoryItemMapper());
    }

    @RequestMapping(method = "delete", value = "{id}")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }

        Query modification = transaction.query("delete from object_modification where item_id = :id");
        modification.setLong("id", id);
        modification.execute();

        Query deleteSockets = transaction.query("delete from object_type_socket where item_id = :id");
        deleteSockets.setLong("id", id);
        deleteSockets.execute();

        Query productions = transaction.query("delete from productions where station in (" +
                "select id from objects where hull_id = :id)");
        productions.setLong("id", id);
        productions.execute();

        Query objects = transaction.query("delete from objects where hull_id = :id");
        objects.setLong("id", id);
        objects.execute();

        Query query = transaction.query("delete from object_type_description where id = :id");
        query.setLong("id", id);
        query.execute();
    }

    @RequestMapping("{id}")
    public InventoryItem getOne(@PathVariable("id") Long itemId, Transaction transaction) {
        Query query = transaction.query("select * from object_type_description where id = :id");
        query.setLong("id", itemId);
        List<InventoryItem> list = query.executeQuery(new InventoryItemMapper());
        if (list.size() == 1) {
            InventoryItem out = list.get(0);
            query = transaction.query("select object_modification_type.* from object_modification" +
                    " left join object_modification_type on object_modification.modification_id = object_modification_type.id" +
                    " where object_modification.item_id = :id");
            query.setLong("id", itemId);
            out.setModifications(query.executeQuery(new InventoryModificationMapper()));

            query = transaction.query("select object_type_socket.* from object_type_socket" +
                    " where object_type_socket.item_id = :id order by object_type_socket.sort_order");
            query.setLong("id", itemId);
            out.setSockets(query.executeQuery(new SocketMapper()));
            return out;
        } else {
            return null;
        }
    }

    private void saveModifications(InventoryItem inventoryItem, Transaction transaction) {
        List<InventoryModification> modifications = inventoryItem.getModifications();
        if (modifications == null || modifications.size() == 0) {
            Query query = transaction.query("delete from object_modification where item_id = :itemId");
            query.setLong("itemId", inventoryItem.getId());
            query.execute();
            return;
        }
        Map<Long, InventoryModification> modificationMap = modifications.stream()
                .collect(Collectors.toMap(InventoryModification::getId, v -> v));

        Query query = transaction.query("select * from object_modification where item_id = :itemId");
        query.setLong("itemId", inventoryItem.getId());
        Map<Long, ManyToMany> join = query.executeQuery(resultSet -> new ManyToMany(
                resultSet.fetchLong("id"),
                resultSet.fetchLong("item_id"),
                resultSet.fetchLong("modification_id")
        )).stream().collect(Collectors.toMap(ManyToMany::getRight, m -> m));

        List<InventoryModification> toAdd = new ArrayList<>();
        List<ManyToMany> toDelete = new ArrayList<>();

        for (InventoryModification modification : modifications) {
            if (!join.containsKey(modification.getId())) {
                toAdd.add(modification);
            }
        }
        for (Map.Entry<Long, ManyToMany> existing : join.entrySet()) {
            if (!modificationMap.containsKey(existing.getKey())) {
                toDelete.add(existing.getValue());
            }
        }
        Query addQuery = transaction.query("insert into object_modification (item_id, modification_id)  values (:itemId, :modificationId)");
        for (InventoryModification modification : toAdd) {
            addQuery.setLong("itemId", inventoryItem.getId());
            addQuery.setLong("modificationId", modification.getId());
            addQuery.addBatch();
        }
        addQuery.executeBatch();

        Query delete = transaction.query("delete from object_modification where id = :id");
        for (ManyToMany manyToMany : toDelete) {
            delete.setLong("id", manyToMany.getId());
            delete.addBatch();
        }
        delete.executeBatch();
    }

    private void saveSockets(InventoryItem inventoryItem, Transaction transaction) {
        List<InventorySocket> sockets = inventoryItem.getSockets();
        if (sockets == null || sockets.isEmpty()) {
            Query del = transaction.query("delete from object_type_socket where item_id = :itemId");
            del.setLong("itemId", inventoryItem.getId());
            del.execute();
            return;
        }


        Query query = transaction.query("select * from object_type_socket where item_id = :itemId");
        query.setLong("itemId", inventoryItem.getId());
        Map<Long, InventorySocket> existing = query.executeQuery(new SocketMapper())
                .stream()
                .collect(Collectors.toMap(InventorySocket::getId, v -> v));

        boolean updateRequired = false;
        Query update = transaction.query("update object_type_socket " +
                "set item_type_id = :itemTypeId, alias = :alias " +
                "where item_id = :itemId and id = :id");
        for(InventorySocket socket : sockets) {
            if(socket.getId() != null) {
                updateRequired = true;
                update.setLong("id", socket.getId());
                update.setLong("itemId", inventoryItem.getId());
                update.setLong("itemTypeId", socket.getItemTypeId());
                update.setString("alias", socket.getAlias());
                update.addBatch();
            }
        }
        if(updateRequired) {
            update.executeBatch();
        }

        Query insert = transaction.query("insert into object_type_socket (item_id, item_type_id, alias)" +
                " value (:itemId, :itemTypeId, :alias)");
        boolean insertRequired = false;
        for(InventorySocket socket : sockets) {
            if(socket.getId() == null) {
                insertRequired = true;
                insert.setLong("itemId", inventoryItem.getId());
                insert.setLong("itemTypeId", socket.getItemTypeId());
                insert.setString("alias", socket.getAlias());
                insert.executeUpdate();
                socket.setId(insert.getLastGeneratedKey(Long.class));
            }
        }
        if(insertRequired) {
            insert.executeBatch();
        }

        Map<Long, InventorySocket> current = sockets.stream()
                .collect(Collectors.toMap(InventorySocket::getId, v -> v));
        Query delete = transaction.query("delete from object_type_socket where id = :id");
        boolean deleteRequired = false;
        for(Map.Entry<Long, InventorySocket> socket : existing.entrySet()) {
            if(!current.containsKey(socket.getKey())) {
                deleteRequired = true;
                delete.setLong("id", socket.getKey());
                delete.addBatch();
            }
        }
        if(deleteRequired) {
            delete.executeBatch();
        }

        Query updateOrder = transaction.query("update object_type_socket set sort_order = :sortOrder where id = :id");
        for(int i = 0; i < sockets.size(); i++) {
            InventorySocket socket = sockets.get(i);
            updateOrder.setInt("sortOrder", i + 1);
            updateOrder.setLong("id", socket.getId());
            updateOrder.executeUpdate();
        }
    }

}

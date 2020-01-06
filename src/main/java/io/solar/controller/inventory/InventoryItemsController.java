package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.InventoryItem;
import io.solar.entity.InventoryModification;
import io.solar.entity.InventoryType;
import io.solar.entity.User;
import io.solar.entity.util.ManyToMany;
import io.solar.mapper.InventoryItemMapper;
import io.solar.mapper.InventoryModificationMapper;
import io.solar.mapper.InventoryTypeMapper;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.SafeResultSet;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "inventory-item")
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

            query = transaction.query("select object_type.* from object_type_socket" +
                    " left join object_type on object_type.id = object_type_socket.item_type_id" +
                    " where object_type_socket.item_id = :id order by object_type_socket.sort_order");
            query.setLong("id", itemId);
            out.setSockets(query.executeQuery(new InventoryTypeMapper()));
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
                resultSet.getLong("id"),
                resultSet.getLong("item_id"),
                resultSet.getLong("modification_id")
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
        List<InventoryType> sockets = inventoryItem.getSockets();
        if (sockets == null || sockets.isEmpty()) {
            Query del = transaction.query("delete from object_type_socket where item_id = :itemId");
            del.setLong("itemId", inventoryItem.getId());
            del.execute();
            return;
        }
        Map<Long, List<InventoryType>> thisMap = new HashMap<>();
        for (InventoryType type : sockets) {
            List<InventoryType> list = thisMap.computeIfAbsent(type.getId(), k -> new ArrayList<>());
            list.add(type);
        }

        Query query = transaction.query("select * from object_type_socket where item_id = :itemId");
        query.setLong("itemId", inventoryItem.getId());
        List<ManyToMany> existingLinks = query.executeQuery((SafeResultSet rs) -> new ManyToMany(
                rs.getLong("id"),
                rs.getLong("item_id"),
                rs.getLong("item_type_id"),
                rs.getInt("sort_order")
        ));
        Map<Long, List<ManyToMany>> existingMap = new HashMap<>();
        for (ManyToMany join : existingLinks) {
            List<ManyToMany> list = existingMap.computeIfAbsent(join.getRight(), k -> new ArrayList<>());
            list.add(join);
        }

        Query delete = transaction.query("delete from object_type_socket where id = :id");
        Query insert = transaction.query("insert into object_type_socket (item_id, item_type_id)" +
                " value (:itemId, :itemTypeId)");

        for (Map.Entry<Long, List<InventoryType>> entry : thisMap.entrySet()) {
            List<ManyToMany> existing = existingMap.get(entry.getKey());
            if (existing == null) {
                existing = new ArrayList<>();
            }
            List<InventoryType> thisType = entry.getValue();
            int delta = existing.size() - thisType.size();
            if (delta > 0) {
                Long id = existing.get(existing.size() - 1).getId();
                while (delta > 0) {
                    delete.setLong("id", id);
                    delete.addBatch();
                    delta--;
                }
            } else if (delta < 0) {
                while (delta < 0) {
                    insert.setLong("itemId", inventoryItem.getId());
                    insert.setLong("itemTypeId", entry.getKey());
                    insert.addBatch();
                    delta++;
                }
            }
        }
        for (Map.Entry<Long, List<ManyToMany>> entry : existingMap.entrySet()) {
            Long itemId = entry.getKey();
            if (!thisMap.containsKey(itemId)) {
                List<ManyToMany> joins = entry.getValue();
                for (ManyToMany join : joins) {
                    delete.setLong("id", join.getId());
                    delete.addBatch();
                }
            }
        }
        delete.executeBatch();
        insert.executeBatch();


        List<InventoryType> incomingOrder = new ArrayList<>(inventoryItem.getSockets());
        Query select = transaction.query("select id, item_id, item_type_id from object_type_socket where item_id = :item_id");
        select.setLong("item_id", inventoryItem.getId());
        List<ManyToMany> dbOrderList = query.executeQuery(rs ->
                new ManyToMany(rs.getLong("id"), rs.getLong("item_id"), rs.getLong("item_type_id"))
        );
        Map<Long, List<ManyToMany>> dbOrderMap = new HashMap<>();
        for(ManyToMany item : dbOrderList) {
            List<ManyToMany> list = dbOrderMap.computeIfAbsent(item.getRight(), v -> new ArrayList<>());
            list.add(item);
        }

        Query update = transaction.query("update object_type_socket set sort_order = :sort_order where id = :id");
        for(int i = 0; i < incomingOrder.size(); i++) {
            List<ManyToMany> list = dbOrderMap.get(incomingOrder.get(i).getId());
            for(ManyToMany m : list) {
                if(m.getSort() == null) {
                    m.setSort(i);
                    update.setInt("sort_order", i + 1);
                    update.setLong("id", m.getId());
                    update.addBatch();
                    break;
                }
            }
        }
        update.executeBatch();
    }

}

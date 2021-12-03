package io.solar.controller.inventory;

import io.solar.dto.inventory.InventoryItemDto;
import io.solar.dto.inventory.InventoryModificationDto;
import io.solar.dto.inventory.InventorySocketDto;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.facade.ObjectTypeDescriptionFacade;
import io.solar.service.ObjectTypeDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transaction;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/inventory-item")
public class InventoryItemsController {

    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final ObjectTypeDescriptionFacade objectTypeDescriptionFacade;

    @Autowired
    public InventoryItemsController(ObjectTypeDescriptionService objectTypeDescriptionService,
                                    ObjectTypeDescriptionFacade objectTypeDescriptionFacade) {

        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.objectTypeDescriptionFacade = objectTypeDescriptionFacade;
    }

    @GetMapping
    public List<InventoryItemDto> getAll() {

        return objectTypeDescriptionFacade.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public ResponseEntity<InventoryItemDto> save(@RequestBody InventoryItemDto inventoryItem) {

        InventoryItemDto savedInventoryItem = objectTypeDescriptionFacade.save(inventoryItem);

//        saveModifications(inventoryItem, transaction);
//        saveSockets(inventoryItem, transaction);

        return ResponseEntity.ok(savedInventoryItem);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_INVENTORY')")
    public void delete(@PathVariable("id") Long id) {
        objectTypeDescriptionFacade.delete(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<InventoryItemDto> getOne(@PathVariable("id") Long itemId) {

        Optional<ObjectTypeDescription> objectOptional = objectTypeDescriptionService.findById(itemId);

        if (objectOptional.isPresent()) {

            ObjectTypeDescription objectTypeDescription = objectOptional.get();

            List<InventoryModificationDto> modifications = objectTypeDescriptionFacade.findAllModifications(objectTypeDescription.getId());

            List<InventorySocketDto> sockets = null; //todo: get all sockets from 'object_type_socket'

//            query = transaction.query("select object_type_socket.* from object_type_socket" +
//                    " where object_type_socket.item_id = :id order by object_type_socket.sort_order");
//            query.setLong("id", itemId);
//            out.setSockets(query.executeQuery(new SocketMapper()));
//
            return null;

        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    private void saveModifications(InventoryItem inventoryItem, Transaction transaction) {
//        List<InventoryModification> modifications = inventoryItem.getModifications();
//        if (modifications == null || modifications.size() == 0) {
//            Query query = transaction.query("delete from object_modification where item_id = :itemId");
//            query.setLong("itemId", inventoryItem.getId());
//            query.execute();
//            return;
//        }
//        Map<Long, InventoryModification> modificationMap = modifications.stream()
//                .collect(Collectors.toMap(InventoryModification::getId, v -> v));
//
//        Query query = transaction.query("select * from object_modification where item_id = :itemId");
//        query.setLong("itemId", inventoryItem.getId());
//        Map<Long, ManyToMany> join = query.executeQuery(resultSet -> new ManyToMany(
//                resultSet.fetchLong("id"),
//                resultSet.fetchLong("item_id"),
//                resultSet.fetchLong("modification_id")
//        )).stream().collect(Collectors.toMap(ManyToMany::getRight, m -> m));
//
//        List<InventoryModification> toAdd = new ArrayList<>();
//        List<ManyToMany> toDelete = new ArrayList<>();
//
//        for (InventoryModification modification : modifications) {
//            if (!join.containsKey(modification.getId())) {
//                toAdd.add(modification);
//            }
//        }
//        for (Map.Entry<Long, ManyToMany> existing : join.entrySet()) {
//            if (!modificationMap.containsKey(existing.getKey())) {
//                toDelete.add(existing.getValue());
//            }
//        }
//        Query addQuery = transaction.query("insert into object_modification (item_id, modification_id)  values (:itemId, :modificationId)");
//        for (InventoryModification modification : toAdd) {
//            addQuery.setLong("itemId", inventoryItem.getId());
//            addQuery.setLong("modificationId", modification.getId());
//            addQuery.addBatch();
//        }
//        addQuery.executeBatch();
//
//        Query delete = transaction.query("delete from object_modification where id = :id");
//        for (ManyToMany manyToMany : toDelete) {
//            delete.setLong("id", manyToMany.getId());
//            delete.addBatch();
//        }
//        delete.executeBatch();
//    }
//
//    private void saveSockets(InventoryItem inventoryItem, Transaction transaction) {
//        List<InventorySocket> sockets = inventoryItem.getSockets();
//        if (sockets == null || sockets.isEmpty()) {
//            Query del = transaction.query("delete from object_type_socket where item_id = :itemId");
//            del.setLong("itemId", inventoryItem.getId());
//            del.execute();
//            return;
//        }
//
//
//        Query query = transaction.query("select * from object_type_socket where item_id = :itemId");
//        query.setLong("itemId", inventoryItem.getId());
//        Map<Long, InventorySocket> existing = query.executeQuery(new SocketMapper())
//                .stream()
//                .collect(Collectors.toMap(InventorySocket::getId, v -> v));
//
//        boolean updateRequired = false;
//        Query update = transaction.query("update object_type_socket " +
//                "set item_type_id = :itemTypeId, alias = :alias " +
//                "where item_id = :itemId and id = :id");
//        for(InventorySocket socket : sockets) {
//            if(socket.getId() != null) {
//                updateRequired = true;
//                update.setLong("id", socket.getId());
//                update.setLong("itemId", inventoryItem.getId());
//                update.setLong("itemTypeId", socket.getItemTypeId());
//                update.setString("alias", socket.getAlias());
//                update.addBatch();
//            }
//        }
//        if(updateRequired) {
//            update.executeBatch();
//        }
//
//        Query insert = transaction.query("insert into object_type_socket (item_id, item_type_id, alias)" +
//                " value (:itemId, :itemTypeId, :alias)");
//        boolean insertRequired = false;
//        for(InventorySocket socket : sockets) {
//            if(socket.getId() == null) {
//                insertRequired = true;
//                insert.setLong("itemId", inventoryItem.getId());
//                insert.setLong("itemTypeId", socket.getItemTypeId());
//                insert.setString("alias", socket.getAlias());
//                insert.executeUpdate();
//                socket.setId(insert.getLastGeneratedKey(Long.class));
//            }
//        }
//        if(insertRequired) {
//            insert.executeBatch();
//        }
//
//        Map<Long, InventorySocket> current = sockets.stream()
//                .collect(Collectors.toMap(InventorySocket::getId, v -> v));
//        Query delete = transaction.query("delete from object_type_socket where id = :id");
//        boolean deleteRequired = false;
//        for(Map.Entry<Long, InventorySocket> socket : existing.entrySet()) {
//            if(!current.containsKey(socket.getKey())) {
//                deleteRequired = true;
//                delete.setLong("id", socket.getKey());
//                delete.addBatch();
//            }
//        }
//        if(deleteRequired) {
//            delete.executeBatch();
//        }
//
//        Query updateOrder = transaction.query("update object_type_socket set sort_order = :sortOrder where id = :id");
//        for(int i = 0; i < sockets.size(); i++) {
//            InventorySocket socket = sockets.get(i);
//            updateOrder.setInt("sortOrder", i + 1);
//            updateOrder.setLong("id", socket.getId());
//            updateOrder.executeUpdate();
//        }
//    }

}

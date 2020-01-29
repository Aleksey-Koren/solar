package io.solar.controller.inventory;

import io.solar.controller.AuthController;
import io.solar.entity.User;
import io.solar.entity.objects.ObjectView;
import io.solar.mapper.objects.ObjectViewMapper;
import io.solar.mapper.TotalMapper;
import io.solar.service.ObjectService;
import io.solar.utils.Page;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import io.solar.utils.server.controller.RequestParam;

import java.util.List;

@Controller
@RequestMapping("objects")
public class ObjectsController {

    private final ObjectService objectService;

    public ObjectsController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @RequestMapping("config")
    public Page<ObjectView> getConfigPage(
            Pageable pageable,
            Transaction transaction,
            @AuthData User user,
            @RequestParam("detached") Boolean detached,
            @RequestParam("inventoryType") Long inventoryType
    ) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        boolean typePresent = inventoryType != null;
        Query query = transaction.query("select " +
                " objects.*" +
                " from objects" +
                " inner join object_type_description on objects.hull_id = object_type_description.id" +
                " where 1 = 1 " +
                        (typePresent ? " and object_type_description.inventory_type = :inventoryType" : "") +
                        (Boolean.TRUE.equals(detached) ? " and objects.attached_to_ship is null and objects.attached_to_socket is null" : "") +
                " order by objects.id limit :skip, :pageSize");
        query.setInt("skip", pageable.getPage() * pageable.getPageSize());
        query.setInt("pageSize", pageable.getPageSize());
        if(typePresent) {
            query.setLong("inventoryType", inventoryType);
        }
        Query countQuery = transaction.query("select count(1) " +
                " from objects" +
                " inner join object_type_description on objects.hull_id = object_type_description.id " +
                " where 1 = 1 " +
                (typePresent ? " and object_type_description.inventory_type = :inventoryType" : ""));
        if(typePresent) {
            countQuery.setLong("inventoryType", inventoryType);
        }

        return new Page<>(
                query.executeQuery(new ObjectViewMapper(transaction, false, false)),
                countQuery.executeQuery(new TotalMapper()).get(0)
        );
    }

    @RequestMapping("config/{id}")
    public ObjectView getConfigItem(
            Pageable pageable,
            Transaction transaction,
            @AuthData User user,
            @PathVariable("id") Long id
    ) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        return get(id, transaction);
    }

    @RequestMapping(value = "config/", method = "post")
    public ObjectView saveItem(
            @RequestBody ObjectView objectView,
            @AuthData User user,
            Transaction transaction
    ) {
        if (!AuthController.userCan(user, "edit-inventory", transaction)) {
            throw new RuntimeException("no privileges");
        }
        objectService.save(objectView, transaction);
        return get(objectView.getId(), transaction);
    }

    private ObjectView get(Long id, Transaction transaction) {
        Query query = transaction.query("select objects.*, object_type_description.title" +
                " from objects " +
                " inner join object_type_description on objects.hull_id = object_type_description.id" +
                " where objects.id = :id");
        query.setLong("id", id);
        List<ObjectView> out = query.executeQuery(new ObjectViewMapper(transaction));
        return out.size() == 1 ? out.get(0) : null;
    }
}

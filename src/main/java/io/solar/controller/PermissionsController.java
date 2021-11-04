package io.solar.controller;


import io.solar.entity.Permission;
import io.solar.entity.PermissionType;
import io.solar.entity.User;
import io.solar.mapper.PermissionMapper;
import io.solar.mapper.PermissionTypeMappe;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Controller
@RequestMapping("/permissions")
public class PermissionsController {

    @RequestMapping(method = "post")
    public PermissionType save(@RequestBody PermissionType permissionType, @AuthData User user, Transaction transaction) {
        if(!AuthController.userCan(user, "edit-permission", transaction)) {
            throw new RuntimeException("no permissions");
        }
        if (permissionType == null || permissionType.getTitle() == null || "".equals(permissionType.getTitle())) {
            throw new RuntimeException("bad request, permission type is blank");
        }

        String q;
        if (permissionType.getId() == null) {
            q = "select * from permission_type where title = :title";
        } else {
            q = "select * from permission_type where title = :title and id != :id";
        }
        Query query = transaction.query(q);
        if (permissionType.getId() != null) {
            query.setLong("id", permissionType.getId());
        }
        query.setString("title", permissionType.getTitle());
        List<PermissionType> existing = query.executeQuery(new PermissionTypeMappe());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        if (permissionType.getId() != null) {
            query = transaction.query("update permission_type set title = :title where id = :id");
            query.setLong("id", permissionType.getId());
        } else {
            query = transaction.query("insert into permission_type (title) values (:title)");
        }
        query.setString("title", permissionType.getTitle());
        query.execute();
        if (permissionType.getId() == null) {
            permissionType.setId(query.getLastGeneratedKey(Long.class));
        }
        return permissionType;
    }

    @RequestMapping
    public List<PermissionType> get(Transaction transaction) {
        Query query = transaction.query("select * from permission_type");
        return query.executeQuery(new PermissionTypeMappe());
    }

    @RequestMapping(value = "elevate", method = "post")
    public Permission elevate(@RequestBody Permission permission, @AuthData User user, Transaction transaction) {
        if(!AuthController.userCan(user, "assign-permission", transaction)) {
            throw new RuntimeException("no permissions");
        }
        if (permission == null || permission.getPermissionTypeId() == null && permission.getUserId() == null) {
            throw new RuntimeException("bad request, could not elevate, permission is blank");
        }
        boolean remove = Boolean.TRUE.equals(permission.getRemove());
        Query query;
        if (!remove) {
            query = transaction.query("select permission.id, permission.user_id, permission.permission_type, permission_type.title" +
                    " from permission" +
                    " inner join permission_type on permission.permission_type = permission_type.id" +
                    " where permission.permission_type = :permission_type and permission.user_id = :user_id");
            query.setLong("permission_type", permission.getPermissionTypeId());
            query.setLong("user_id", permission.getUserId());
            List<Permission> existing = query.executeQuery(new PermissionMapper());
            if (!existing.isEmpty()) {
                return existing.get(0);
            }
        }

        if (remove) {
            query = transaction.query("delete from permission where user_id = :user_id and permission_type = :permission_type");
        } else {
            query = transaction.query("insert into permission (user_id, permission_type) values (:user_id, :permission_type)");
        }
        query.setLong("permission_type", permission.getPermissionTypeId());
        query.setLong("user_id", permission.getUserId());
        query.execute();
        if(remove) {
            return null;
        } else {
            if(permission.getId() == null) {
                permission.setId(query.getLastGeneratedKey(Long.class));
            }
            return permission;
        }
    }

    @RequestMapping("user/{userId}")
    public List<Permission> userPermissions(@PathVariable("userId") Long userId, @AuthData User user, Transaction transaction) {
        if(!(userId.equals(user.getId()) || AuthController.userCan(user, "see-permissions", transaction))) {
            throw new RuntimeException("no access");
        }
        Query query = transaction.query("select permission.id, permission.user_id, permission.permission_type, permission_type.title" +
                " from permission" +
                " inner join permission_type on permission.permission_type = permission_type.id" +
                " where permission.user_id = :user_id");
        query.setLong("user_id", userId);
        return query.executeQuery(new PermissionMapper());
    }

}

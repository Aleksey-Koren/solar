package io.solar.mapper;

import io.solar.entity.Permission;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PermissionMapper implements DbMapper<Permission> {

    @Override
    public Permission map(SafeResultSet resultSet) {
        Permission permission = new Permission();
        permission.setId(resultSet.getLong("id"));
        permission.setPermissionTypeId(resultSet.getLong("permission_type"));
        permission.setUserId(resultSet.getLong("user_id"));
        permission.setTitle(resultSet.getString("title"));
        permission.setRemove(false);
        return permission;
    }
}

package io.solar.mapper;

import io.solar.entity.Permission;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PermissionMapper implements DbMapper<Permission> {

    @Override
    public Permission map(SafeResultSet resultSet) {
        Permission permission = new Permission();
        permission.setId(resultSet.fetchLong("id"));
        permission.setPermissionTypeId(resultSet.fetchLong("permission_type"));
        permission.setUserId(resultSet.fetchLong("user_id"));
        permission.setTitle(resultSet.getString("title"));
        permission.setRemove(false);
        return permission;
    }
}

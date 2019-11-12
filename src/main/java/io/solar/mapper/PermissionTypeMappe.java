package io.solar.mapper;

import io.solar.entity.PermissionType;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PermissionTypeMappe implements DbMapper<PermissionType> {
    @Override
    public PermissionType map(SafeResultSet resultSet) {
        PermissionType out = new PermissionType();
        out.setId(resultSet.getLong("id"));
        out.setTitle(resultSet.getString("title"));
        return out;
    }
}

package io.solar.mapper;

import io.solar.entity.InventoryType;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class InventoryTypeMapper implements DbMapper<InventoryType> {
    @Override
    public InventoryType map(SafeResultSet resultSet) {
        InventoryType out = new InventoryType();
        out.setId(resultSet.getLong("id"));
        out.setTitle(resultSet.getString("title"));
        return out;
    }
}

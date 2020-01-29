package io.solar.mapper;

import io.solar.entity.inventory.InventoryType;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class InventoryTypeMapper implements DbMapper<InventoryType> {
    @Override
    public InventoryType map(SafeResultSet resultSet) {
        InventoryType out = new InventoryType();
        out.setId(resultSet.fetchLong("id"));
        out.setTitle(resultSet.getString("title"));
        return out;
    }
}

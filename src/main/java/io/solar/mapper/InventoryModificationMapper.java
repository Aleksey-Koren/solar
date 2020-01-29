package io.solar.mapper;

import io.solar.entity.inventory.InventoryModification;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class InventoryModificationMapper implements DbMapper<InventoryModification> {
    @Override
    public InventoryModification map(SafeResultSet resultSet) {
        InventoryModification out = new InventoryModification();

        out.setId(resultSet.fetchLong("id"));
        out.setTitle(resultSet.getString("title"));
        out.setData(resultSet.getString("data"));
        out.setDescription(resultSet.getString("description"));

        return out;
    }
}

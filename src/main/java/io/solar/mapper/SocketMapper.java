package io.solar.mapper;

import io.solar.entity.inventory.InventorySocket;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class SocketMapper implements DbMapper<InventorySocket> {
    @Override
    public InventorySocket map(SafeResultSet resultSet) {
        InventorySocket out = new InventorySocket();
        out.setId(resultSet.fetchLong("id"));
        out.setItemId(resultSet.fetchLong("item_id"));
        out.setItemTypeId(resultSet.fetchLong("item_type_id"));
        out.setAlias(resultSet.getString("alias"));
        out.setSortOrder(resultSet.getInt("sort_order"));

        return out;
    }
}

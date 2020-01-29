package io.solar.mapper;

import io.solar.entity.inventory.InventoryItem;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class InventoryItemMapper implements DbMapper<InventoryItem> {

    @Override
    public InventoryItem map(SafeResultSet resultSet) {
        InventoryItem out = new InventoryItem();

        out.setId(resultSet.fetchLong("id"));
        out.setInventoryType(resultSet.fetchLong("inventory_type"));
        out.setTitle(resultSet.getString("title"));
        out.setPowerMin(resultSet.fetchFloat("power_min"));
        out.setPowerMax(resultSet.fetchFloat("power_max"));
        out.setPowerDegradation(resultSet.fetchFloat("power_degradation"));
        out.setCooldown(resultSet.fetchFloat("cooldown"));
        out.setDistance(resultSet.fetchLong("distance"));
        out.setEnergyConsumption(resultSet.fetchLong("energy_consumption"));
        out.setDurability(resultSet.fetchLong("durability"));
        out.setMass(resultSet.fetchLong("mass"));
        out.setDescription(resultSet.getString("description"));
        out.setPrice(resultSet.fetchLong("price"));

        return out;
    }
}

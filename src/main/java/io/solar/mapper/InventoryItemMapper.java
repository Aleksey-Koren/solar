package io.solar.mapper;

import io.solar.entity.InventoryItem;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class InventoryItemMapper implements DbMapper<InventoryItem> {

    @Override
    public InventoryItem map(SafeResultSet resultSet) {
        InventoryItem out = new InventoryItem();

        out.setId(resultSet.getLong("id"));
        out.setInventoryType(resultSet.getLong("inventory_type"));
        out.setTitle(resultSet.getString("title"));
        out.setPowerMin(resultSet.getFloat("power_min"));
        out.setPowerMax(resultSet.getFloat("power_max"));
        out.setPowerDegradation(resultSet.getFloat("power_degradation"));
        out.setCooldown(resultSet.getFloat("cooldown"));
        out.setDistance(resultSet.getLong("distance"));
        out.setEnergyConsumption(resultSet.getLong("energy_consumption"));
        out.setDurability(resultSet.getLong("durability"));
        out.setMass(resultSet.getLong("mass"));
        out.setDescription(resultSet.getString("description"));
        out.setPrice(resultSet.getLong("price"));

        return out;
    }
}

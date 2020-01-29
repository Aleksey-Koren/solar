package io.solar.mapper;

import io.solar.entity.objects.StarShip;
import io.solar.utils.ObjectUtils;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class StarShipMapper implements DbMapper<StarShip> {

    @Override
    public StarShip map(SafeResultSet resultSet) {
        StarShip out = new StarShip();
        ObjectUtils.populate(out, resultSet);

        out.setHullTitle(resultSet.getString("hull_title"));
        out.setPowerDegradation(resultSet.fetchFloat("hull_power_degradation"));
        out.setEnergyConsumption(resultSet.fetchFloat("hull_energy_consumption"));
        out.setMaxDurability(resultSet.fetchLong("hull_durability"));
        out.setHullDescription(resultSet.getString("hull_description"));

        return out;
    }
}

package io.solar.mapper;

import io.solar.entity.StarShip;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class StarShipMapper implements DbMapper<StarShip> {

    @Override
    public StarShip map(SafeResultSet resultSet) {
        StarShip out = new StarShip();
        out.setId(resultSet.getLong("user_ship_id"));
        out.setPlanet(resultSet.getLong("user_ship_planet"));
        out.setPopulation(resultSet.getLong("user_ship_population"));
        out.setFraction(resultSet.getString("user_ship_fraction"));
        out.setTitle(resultSet.getString("user_ship_title"));
        out.setX(resultSet.getFloat("user_ship_x"));
        out.setY(resultSet.getFloat("user_ship_y"));
        out.setAphelion(resultSet.getFloat("user_ship_aphelion"));
        out.setOrbitalPeriod(resultSet.getFloat("user_ship_aphelion"));
        out.setAngle(resultSet.getFloat("user_ship_angle"));
        out.setType(resultSet.getString("hull_type"));
        out.setUserId(resultSet.getLong("user_ship_user_id"));
        out.setActive(Boolean.TRUE.equals(resultSet.getBoolean("user_ship_active")));
        out.setDurability(resultSet.getLong("user_ship_durability"));
        out.setHullId(resultSet.getLong("hull_id"));
        out.setHullTitle(resultSet.getString("hull_title"));
        out.setPowerDegradation(resultSet.getFloat("hull_power_degradation"));
        out.setEnergyConsumption(resultSet.getFloat("hull_energy_consumption"));
        out.setMaxDurability(resultSet.getLong("hull_durability"));
        out.setHullDescription(resultSet.getString("hull_description"));

        return out;
    }
}

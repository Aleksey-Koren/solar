package io.solar.mapper;

import io.solar.entity.Station;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class StationMapper implements DbMapper<Station> {
    @Override
    public Station map(SafeResultSet resultSet) {
        Station station = new Station();

        station.setId(resultSet.getLong("id"));
        station.setTitle(resultSet.getString("title"));
        station.setPlanetId(resultSet.getLong("planet"));
        station.setPopulation(resultSet.getLong("population"));
        station.setFraction(resultSet.getString("fraction"));
        station.setType(resultSet.getString("type"));
        station.setX(resultSet.getFloat("x"));
        station.setY(resultSet.getFloat("y"));
        station.setAphelion(resultSet.getFloat("aphelion"));
        station.setAngle(resultSet.getFloat("angle"));
        station.setOrbitalPeriod(resultSet.getFloat("orbital_period"));
        station.setHullId(resultSet.getLong("hull_id"));

        return station;
    }
}

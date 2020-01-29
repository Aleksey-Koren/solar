package io.solar.mapper;

import io.solar.entity.objects.Station;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class StationMapper implements DbMapper<Station> {
    @Override
    public Station map(SafeResultSet resultSet) {
        Station station = new Station();

        station.setId(resultSet.fetchLong("id"));
        station.setTitle(resultSet.getString("title"));
        station.setPlanet(resultSet.fetchLong("planet"));
        station.setPopulation(resultSet.fetchLong("population"));
        station.setFraction(resultSet.getString("fraction"));
        station.setX(resultSet.fetchFloat("x"));
        station.setY(resultSet.fetchFloat("y"));
        station.setAphelion(resultSet.fetchFloat("aphelion"));
        station.setAngle(resultSet.fetchFloat("angle"));
        station.setOrbitalPeriod(resultSet.fetchFloat("orbital_period"));
        station.setHullId(resultSet.fetchLong("hull_id"));

        return station;
    }
}

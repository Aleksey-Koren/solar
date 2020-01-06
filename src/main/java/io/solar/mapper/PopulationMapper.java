package io.solar.mapper;

import io.solar.entity.StarShip;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PopulationMapper implements DbMapper<StarShip> {
    @Override
    public StarShip map(SafeResultSet resultSet) {
        StarShip out = new StarShip();
        out.setId(resultSet.getLong("id"));
        out.setPopulation(resultSet.getLong("population"));
        return out;
    }
}

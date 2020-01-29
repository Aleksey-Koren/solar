package io.solar.mapper;

import io.solar.entity.objects.StarShip;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class PopulationMapper implements DbMapper<StarShip> {
    @Override
    public StarShip map(SafeResultSet resultSet) {
        StarShip out = new StarShip();
        out.setId(resultSet.fetchLong("id"));
        out.setPopulation(resultSet.fetchLong("population"));
        return out;
    }
}

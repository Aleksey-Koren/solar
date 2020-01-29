package io.solar.mapper;

import io.solar.entity.Production;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class ProductionMapper implements DbMapper<Production> {

    @Override
    public Production map(SafeResultSet resultSet) {
        Production production = new Production();
        production.setId(resultSet.fetchLong("id"));
        production.setPower(resultSet.fetchFloat("power"));
        production.setProduct(resultSet.fetchLong("product"));
        production.setStation(resultSet.fetchLong("station"));
        return production;
    }
}

package io.solar.mapper;

import io.solar.entity.Production;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class ProductionMapper implements DbMapper<Production> {

    @Override
    public Production map(SafeResultSet resultSet) {
        Production production = new Production();
        production.setId(resultSet.getLong("id"));
        production.setPower(resultSet.getFloat("power"));
        production.setProduct(resultSet.getLong("product"));
        production.setStation(resultSet.getLong("station"));
        return production;
    }
}

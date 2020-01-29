package io.solar.mapper;

import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class TotalMapper implements DbMapper<Long> {


    @Override
    public Long map(SafeResultSet resultSet) {
        return resultSet.fetchLong(1);
    }
}

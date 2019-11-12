package io.solar.utils.db;

import java.sql.ResultSet;

public interface DbMapper<T> {
    T map(SafeResultSet resultSet);
}

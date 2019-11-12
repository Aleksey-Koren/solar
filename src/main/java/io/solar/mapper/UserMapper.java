package io.solar.mapper;

import io.solar.entity.User;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

public class UserMapper implements DbMapper<User> {
    @Override
    public User map(SafeResultSet resultSet) {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("password"));
        user.setTitle(resultSet.getString("title"));
        return user;
    }
}

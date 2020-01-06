package io.solar.mapper;

import io.solar.entity.User;
import io.solar.utils.db.DbMapper;
import io.solar.utils.db.SafeResultSet;

import java.sql.Timestamp;
import java.time.Instant;

public class UserMapper implements DbMapper<User> {
    @Override
    public User map(SafeResultSet resultSet) {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("password"));
        user.setTitle(resultSet.getString("title"));
        user.setMoney(resultSet.getLong("money"));
        user.setPlanet(resultSet.getLong("planet"));
        user.setHackAttempts(resultSet.getInt("hack_attempts"));
        Timestamp hackBlock = resultSet.getTimestamp("hack_block");
        user.setHackBlock(hackBlock != null ? Instant.ofEpochMilli(hackBlock.getTime()) : null);
        return user;
    }
}

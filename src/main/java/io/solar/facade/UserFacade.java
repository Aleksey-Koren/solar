package io.solar.facade;

import io.solar.dto.UserDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserFacade(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public UserDto updateOnlyTitle(UserDto dto) {
        User user = userService.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        //TODO What should I do if some of this parameters are NULL in dto. Should I check them, of directly set them one-to-one
        user.setTitle(dto.getTitle());
        userService.update(user); //TODO Do we need this line since our controller method is @Transactional (I suppose that we do)?
        return userMapper.toDto(user);
    }

    public UserDto updateGameParameters(UserDto dto) {
        User user = userService.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        //TODO What should I do if some of this parameters are NULL in dto. Should I check them, of directly set them one-to-one
        user.setTitle(dto.getTitle());
        user.setMoney(dto.getMoney());
        userService.update(user); //TODO Do we need this line since our controller method is @Transactional (I suppose that we do)?
        return userMapper.toDto(user);
    }

    public boolean userHasPermission(User user, String permissionTitle) {
        return user.getPermissions().stream().map(Permission::getTitle).anyMatch(s -> s.equals(permissionTitle));
    }
}

package io.solar.facade;

import io.solar.dto.UserDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.mapper.objects.BasicObjectViewMapper;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;

    public UserDto updateOnlyTitle(UserDto dto) {
        User user = userService.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        user.setTitle(dto.getTitle());
        userService.update(user);
        return userMapper.toDto(user);
    }

    public UserDto updateGameParameters(UserDto dto) {
        User user = userService.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        user.setTitle(dto.getTitle());
        user.setMoney(dto.getMoney());
        user.setLocation(basicObjectViewMapper.toEntity(dto.getLocation()));
        userService.update(user);
        return userMapper.toDto(user);
    }

    public boolean userHasPermission(User user, String permissionTitle) {
        return user.getPermissions().stream().map(Permission::getTitle).anyMatch(s -> s.equals(permissionTitle));
    }
}

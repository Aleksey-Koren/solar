package io.solar.facade;

import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static io.solar.controller.AuthController.hasPermissions;

@Component
@Transactional
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserFacade(UserService userService,
                      UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    public UserDto update(UserDto dto, Principal principal) {
        User user = userMapper.toEntity(dto);
        User out;
        if (user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specify the user to edit");
        }

        if (user.getTitle() != null) {
            User userFromPrincipal = userService.findByLogin(principal.getName());
            if (!(userFromPrincipal.getId().equals(user.getId()) || hasPermissions(List.of("EDIT_USER")))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to edit user's title");
            }
            out = userService.updateUserTitle(user);
            return userMapper.toDto(out);
        }

        if (user.getPermissions() != null) {
            // 'hasPermissions check' moved to userService as @PreAuthorize
            out = userService.updateUserPermissions(user);
            return userMapper.toDto(out);
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No new data specified for user");
    }
}

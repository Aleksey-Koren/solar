package io.solar.facade;

import io.solar.dto.UserDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.service.UserService;
import io.solar.service.messenger.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;
    private final RoomService roomService;

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

    public void deleteUser(Long userId) {
        Optional<User> userOptional = userService.findById(userId);

        userOptional.ifPresent(user -> {
            roomService.deleteRoomsWithOneParticipantByUserRooms(user);
            userService.delete(user);
        });
    }

    public void decreaseUserBalance(User user, Long amount) {
        if (user.getMoney() < amount) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Not enough credits at user's balance");
        } else {
            user.setMoney(user.getMoney() - amount);
            userService.update(user);
        }
    }

    public void increaseUserBalance(User user, Long amount) {
        user.setMoney(user.getMoney() + amount);
        userService.update(user);
    }

    public UserDto getById(Long id) {
        return userMapper.toDto(userService.getById(id));
    }
}
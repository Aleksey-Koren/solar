package io.solar.mapper;

import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;
    private final PermissionMapper permissionMapper;

    public User toEntity(UserDto dto) {
        User user;
        if (dto.getId() != null) {
            user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
            user.setTitle(dto.getTitle());
            user.setLogin(dto.getLogin());
            user.setPassword(dto.getPassword());
            user.setMoney(dto.getMoney());
            user.setLocation(dto.getLocation());
            user.setHackBlock(dto.getHackBlock());
            user.setHackAttempts(dto.getHackAttempts());
            user.setAvatar(dto.getAvatar());
            user.setPermissions(dto.getPermissions() == null ? null : dto.getPermissions().stream().map(permissionMapper::toEntity).collect(toSet()));
        }else{
            user = new User(null, dto.getTitle(), dto.getLogin(), dto.getPassword(), dto.getMoney(),
                    dto.getLocation(), dto.getHackBlock(), dto.getHackAttempts(), dto.getAvatar(),
                    dto.getPermissions() == null ? null : dto.getPermissions().stream().map(permissionMapper::toEntity).collect(toSet()));
        }
        return user;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getTitle(), user.getLogin(), null, user.getMoney(),
                user.getLocation(), user.getHackBlock(), user.getHackAttempts(), user.getAvatar(),
                user.getPermissions().stream().map(permissionMapper::toDto).collect(Collectors.toSet()));
    }
 }
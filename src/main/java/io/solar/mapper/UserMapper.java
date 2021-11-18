package io.solar.mapper;

import io.solar.dto.PermissionDto;
import io.solar.dto.UserDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final UserRepository userRepository;
    private final PermissionMapper permissionMapper;

    @Autowired
    public UserMapper(UserRepository userRepository,
                      PermissionMapper permissionMapper) {
        this.userRepository = userRepository;
        this.permissionMapper = permissionMapper;
    }

    public User toEntity(UserDto dto) {
        User user;
        Set<PermissionDto> permissionDtos = dto.getPermissions();
        Set<Permission> permissions = null;
        if (permissionDtos != null) {
            permissions = dto.getPermissions()
                    .stream().map(permissionMapper::toEntity).collect(Collectors.toSet());
        }

        if (dto.getId() != null) {
            user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
            user.setTitle(dto.getTitle());
            user.setLogin(dto.getLogin());
            user.setPassword(dto.getPassword());
            user.setMoney(dto.getMoney());
            user.setPlanet(dto.getPlanet());
            user.setHackBlock(dto.getHackBlock());
            user.setHackAttempts(dto.getHackAttempts());
            user.setPermissions(permissions);
        } else {
            user = new User(null, dto.getTitle(), dto.getLogin(), dto.getPassword(), dto.getMoney(),
                    dto.getPlanet(), dto.getHackBlock(), dto.getHackAttempts(),  permissions);
        }
        return user;
    }

    public UserDto toDto (User user) {
        Set<PermissionDto> permissionDtos = user.getPermissions()
                .stream().map(permissionMapper::toDto).collect(Collectors.toSet());

        return new UserDto(user.getId(), user.getTitle(), user.getLogin(), user.getPassword(), user.getMoney()
                , user.getPlanet(), user.getHackBlock(), user.getHackAttempts(), permissionDtos);
    }
 }

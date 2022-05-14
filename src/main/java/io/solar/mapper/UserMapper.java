package io.solar.mapper;

import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.repository.UserRepository;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;
    private final BasicObjectService basicObjectService;
    private final PermissionMapper permissionMapper;

    public User toEntity(UserDto dto) {

        User user;
        if (dto.getId() != null) {
            user = userRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        } else {
            user = new User();
        }

        user.setTitle(dto.getTitle());
        user.setLogin(dto.getLogin());
        user.setPassword(dto.getPassword());
        user.setMoney(dto.getMoney());
        user.setLocation(dto.getLocationId() == null ? null : basicObjectService.getById(dto.getLocationId()));
        user.setHackBlock(dto.getHackBlock());
        user.setHackAttempts(dto.getHackAttempts());
        user.setAvatar(dto.getAvatar());
        user.setEmailNotifications(dto.getEmailNotifications());
        user.setEmail(dto.getEmail());
        user.setPermissions(dto.getPermissions() != null ? dto.getPermissions().stream().map(permissionMapper::toEntity).collect(Collectors.toSet()) : null);
        
        return user;
    }

    public UserDto toDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .title(user.getTitle())
                .email(user.getEmail())
                .money(user.getMoney())
                .locationId(user.getLocation() == null ? null : user.getLocation().getId())
                .hackBlock(user.getHackBlock())
                .hackAttempts(user.getHackAttempts())
                .avatar(user.getAvatar())
                .emailNotifications(user.getEmailNotifications())
                .permissions(user.getPermissions().stream().map(permissionMapper::toDto).toList())
                .build();
    }

    public UserDto toDtoWithIdAndTitle(User user) {

        return UserDto.builder()
                .id(user.getId())
                .title(user.getTitle())
                .build();
    }
}
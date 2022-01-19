package io.solar.mapper;

import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final UserRepository userRepository;
    private final BasicObjectViewMapper basicObjectViewMapper;

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
        user.setLocation(dto.getLocation() == null ? null : basicObjectViewMapper.toEntity(dto.getLocation()));
        user.setHackBlock(dto.getHackBlock());
        user.setHackAttempts(dto.getHackAttempts());
        user.setAvatar(dto.getAvatar());
        user.setEmailNotifications(dto.getEmailNotifications());
        user.setEmail(dto.getEmail());

        return user;
    }

    public UserDto toDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .title(user.getTitle())
                .email(user.getEmail())
                .money(user.getMoney())
                .location(user.getLocation() == null ? null : basicObjectViewMapper.toDto(user.getLocation()))
                .hackBlock(user.getHackBlock())
                .hackAttempts(user.getHackAttempts())
                .avatar(user.getAvatar())
                .emailNotifications(user.getEmailNotifications())
                .build();
    }

    public UserDto toDtoWithIdAndTitle(User user) {

        return UserDto.builder()
                .id(user.getId())
                .title(user.getTitle())
                .build();
    }
}
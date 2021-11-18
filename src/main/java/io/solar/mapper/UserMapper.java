package io.solar.mapper;

import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserMapper {

    private final UserRepository userRepository;

    @Autowired
    public UserMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User toEntity(UserDto dto) {
        User user;
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
        }else{
            user = new User(null, dto.getTitle(), dto.getLogin(), dto.getPassword(), dto.getMoney(),
                    dto.getPlanet(), dto.getHackBlock(), dto.getHackAttempts(), null);
        }
        return user;
    }

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getTitle(), user.getLogin(), null, user.getMoney(),
                user.getPlanet(), user.getHackBlock(), user.getHackAttempts());
    }
 }

package io.solar.service;

import io.solar.dto.UserDto;
import io.solar.dto.UserFilter;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.repository.PermissionRepository;
import io.solar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PermissionRepository permissionRepository;

    @Value("${app.hack_block_time_min}")
    private Integer HACK_BLOCK_TIME_MIN;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.permissionRepository = permissionRepository;
    }

    public Optional<User> findById (Long id) {
        return userRepository.findById(id);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User register(User user) {
        resetHackAttempts(user);
        user.setPermissions(Set.of(permissionRepository.findByTitle("PLAY_THE_GAME")));
        return userRepository.save(user);
    }

    public User update(User user) {
       return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return User.retrieveUserDetails(userRepository.findByLogin(login));
    }

    public void registerHackAttempt(User user) {
        user.setHackAttempts(user.getHackAttempts() + 1);
        if (user.getHackAttempts() > 4) {
            user.setHackBlock(Instant.now().plusSeconds(HACK_BLOCK_TIME_MIN * 60));
        }
        userRepository.save(user);
    }
    
    public void resetHackAttempts(User user) {
        user.setHackAttempts(0);
            user.setHackBlock(LocalDateTime.of(2010, 1, 1, 0, 0, 0)
                    .toInstant(ZoneOffset.ofTotalSeconds(0)));
    }
    
    public Page<UserDto> getAllUsers(Pageable pageable, UserFilter filter, boolean canEdit) {
        if(!canEdit) {
            filter.setLogin("");
        }

        Page<User> users = userRepository.findAll(new UserSpecification(filter), pageable);
        users.map(u -> mapUser(u, canEdit));
        return users.map(userMapper::toDto);
    }

    public User getUserById(Long id, boolean canEdit) {
        Optional<User> user = userRepository.findById(id);

        return mapUser(
                user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id")),
                canEdit);
    }

    public User updateUserTitle(User user) {
       return userRepository.save(user);
    }

    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    public User updateUserPermissions(User user) {
        return userRepository.save(user);
    }

    private User mapUser(User user, boolean canEdit) {
        if (user.getTitle() == null || user.getTitle().equals("")) {
            String log = user.getLogin();
            int index = log.indexOf("@");
            user.setTitle(index > -1 ? log.substring(0, user.getLogin().indexOf("@")) : log);
        }
        user.setPassword("");
        if (!canEdit) {
            user.setLogin("");
        }
        return user;
    }
}

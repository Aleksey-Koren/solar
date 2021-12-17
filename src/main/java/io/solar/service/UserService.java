package io.solar.service;

import io.solar.dto.UserDto;
import io.solar.dto.UserFilter;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.mapper.UserMapper;
import io.solar.repository.PermissionRepository;
import io.solar.repository.UserRepository;
import io.solar.security.Role;
import io.solar.service.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.hack_block_time_min}")
    private Integer HACK_BLOCK_TIME_MIN;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findById (Long id) {
        return userRepository.findById(id);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User registerNewUser(User user, Role role) {
        if (findByLogin(user.getLogin()) != null) {
            throw new ServiceException("User with such login already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        resetHackAttempts(user);
        Set<Permission> permissions = role.getPermissions().stream()
                                                                .map(s -> permissionRepository.findByTitle(s))
                                                                .collect(toSet());
        user.setPermissions(permissions);
        return userRepository.save(user);
    }

    public User update(User user) {
       return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
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

    public boolean matchPasswords(User user, User userFromDb) {
        return passwordEncoder.matches(user.getPassword(), userFromDb.getPassword());
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

    public boolean isUserLocatedInObject(User user, Long objectId) {
        return user.getLocation().getId().equals(objectId);
    }

    public User updateUserTitle(Long id, String title) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id"));

        user.setTitle(title);
        user = userRepository.save(user);
        return mapUser(user, true);
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

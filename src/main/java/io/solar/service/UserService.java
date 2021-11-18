package io.solar.service;

import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.repository.PermissionRepository;
import io.solar.repository.UserRepository;
import io.solar.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import static java.util.stream.Collectors.*;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

    @Value("${app.hack_block_time_min}")
    private Integer HACK_BLOCK_TIME_MIN;

    @Autowired
    public UserService(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
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
        Set<Permission> permissions = Role.USER.getPermissions().stream()
                                                                .map(s -> permissionRepository.findByTitle(s))
                                                                .collect(toSet());
        user.setPermissions(permissions);
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
    
    public Page<User> getAllUsers(Pageable pageable, String login, String title, boolean canEdit) {
        if(!canEdit) {
            login = "";
        }

        Page<User> users = userRepository.findAll(
                where(UserSpecifications.loginStartsWith(login)
                 .and(UserSpecifications.titleStartsWith(title))),
                pageable);

        users.map(u -> mapUser(u, canEdit));
        return users;
    }

    public User getUserById(Long id, boolean canEdit) {
        Optional<User> user = userRepository.findById(id);

        return mapUser(
                user.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id")),
                canEdit);
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

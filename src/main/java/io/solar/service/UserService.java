package io.solar.service;

import io.solar.dto.ChangePasswordDto;
import io.solar.dto.UserDto;
import io.solar.entity.PasswordToken;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.UserMapper;
import io.solar.repository.PasswordTokenRepository;
import io.solar.repository.PermissionRepository;
import io.solar.repository.UserRepository;
import io.solar.security.Role;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exception.ServiceException;
import io.solar.specification.UserSpecification;
import io.solar.specification.filter.UserFilter;
import lombok.RequiredArgsConstructor;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationEngine notificationEngine;

    @Value("${app.hack_block_time_min}")
    private Integer HACK_BLOCK_TIME_MIN;

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with id = %d in database", User.class.getSimpleName(), userId))
                );
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return User.retrieveUserDetails(userRepository.findByLogin(login));
    }

    public User registerNewUser(User user, Role role) {

        if (findByLogin(user.getLogin()) != null) {
            throw new ServiceException("User with such login already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        resetHackAttempts(user);

        Set<Permission> permissions = role.getPermissions().stream()
                .map(permissionRepository::findByTitle)
                .collect(toSet());

        if (user.getTitle() == null || user.getTitle().equals("")) {
            int index = user.getLogin().indexOf("@");
            user.setTitle(index > 3 ? user.getLogin().substring(0, user.getLogin().indexOf("@")) : user.getLogin());
        }

        user.setPermissions(permissions);
        user.setEmail(receiveEmailFromLogin(user));

        return userRepository.save(user);
    }

    public User changePassword(ChangePasswordDto changePasswordDto) {
        PasswordToken passwordToken = passwordTokenRepository.findByToken(changePasswordDto.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find token"));

        if (passwordToken.getIsActivated()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The token has already been used");
        }

        return updateUserPassword(passwordToken, changePasswordDto.getNewPassword());
    }

    public void decreaseUserBalance(User user, Long amount) {
        if (user.getMoney() < amount) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Not enough credits at user's balance");
        } else {
            user.setMoney(user.getMoney() - amount);
            update(user);
            notificationEngine.notificationToUser(NotificationType.MONEY_UPDATED, user, null);
        }
    }

    public void increaseUserBalance(User user, Long amount) {
        user.setMoney(user.getMoney() + amount);
        update(user);
        notificationEngine.notificationToUser(NotificationType.MONEY_UPDATED, user, null);
    }

    public List<MessageType> getMessageTypesToEmail(User user) {
        if (user.getEmailNotifications() == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(MessageType.values())
                .filter(s -> (!s.equals(MessageType.CHAT) && (user.getEmailNotifications() & s.getIndex()) == s.getIndex()))
                .collect(toList());
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

    public void saveEmailNotifications(User user, List<MessageType> messageTypes) {
        user.setEmailNotifications(calculateEmailNotifications(messageTypes));
        userRepository.save(user);
    }

    public Page<UserDto> getAllUsers(Pageable pageable, UserFilter filter) {
        Page<User> users = userRepository.findAll(new UserSpecification(filter), pageable);

        return users.map(userMapper::toDto);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User update(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public boolean matchPasswords(User user, User userFromDb) {
        return passwordEncoder.matches(user.getPassword(), userFromDb.getPassword());
    }

    public boolean isUserNotLocatedInObject(User user, Long objectId) {
        return !user.getLocation().getId().equals(objectId);
    }

    private User updateUserPassword(PasswordToken passwordToken, String newPassword) {
        User user = passwordToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        passwordToken.setIsActivated(true);

        userRepository.save(user);
        passwordTokenRepository.save(passwordToken);

        return user;
    }

    private Integer calculateEmailNotifications(List<MessageType> messageTypes) {

        return messageTypes.stream()
                .mapToInt(MessageType::getIndex)
                .sum();
    }

    private String receiveEmailFromLogin(User user) {

        return user.getLogin().contains("@")
                ? user.getLogin()
                : null;
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}

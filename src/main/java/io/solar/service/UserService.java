package io.solar.service;

import io.solar.entity.User;
import io.solar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    private final Long HACK_BLOCK_TIME = 300L;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
    }

    public Optional<User> findById (Long id) {
        return userRepository.findById(id);
    }

    public User registerUser(User user) {
        resetHackAttempts(user);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login);
    }

    public void registerHackAttempt(User user) {
        user.setHackAttempts(user.getHackAttempts() + 1);
        if (user.getHackAttempts() > 4) {
            user.setHackBlock(Instant.now().plusSeconds(HACK_BLOCK_TIME));
        }
        userRepository.save(user);
    }

    public void resetHackAttempts(User user) {
        user.setHackAttempts(0);
        try {
            user.setHackBlock(new SimpleDateFormat("yyyyMMddHHmmss").parse("2010-01-01 00:00:00").toInstant());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

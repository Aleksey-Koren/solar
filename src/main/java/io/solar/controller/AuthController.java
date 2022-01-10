package io.solar.controller;


import io.solar.dto.BlockedToken;
import io.solar.dto.Register;
import io.solar.dto.Token;
import io.solar.dto.UserDto;
import io.solar.entity.User;
import io.solar.mapper.UserMapper;
import io.solar.security.JwtProvider;
import io.solar.security.Role;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @Transactional
    @PostMapping("/register")
    public Register register(@RequestBody UserDto dto) {
        User userFromClient = userMapper.toEntity(dto);
        if (userFromClient.getLogin().equals("admin")) {
            return new Register(false, "", "Login \"admin\" is reserved. You have to choose another login");
        }
        User userFromDb = userService.findByLogin(userFromClient.getLogin());
        if (userFromDb != null) {
            return new Register(false, "", "User with this login already exists");
        }
        userFromClient = userService.registerNewUser(userFromClient, Role.USER);
        Token token = createToken(userFromClient);
        return new Register(true, token.getData(), "");
    }

    @Transactional
    @PostMapping("/login")
    public Token login(@RequestBody UserDto dto) {
        User userFromClient = userMapper.toEntity(dto);
        User userFromDb = userService.findByLogin(userFromClient.getLogin());
        if (userFromDb != null) {
            Instant now = Instant.now();
            if (isHackBlocked(userFromDb, now)) {
                return new BlockedToken(userFromDb.getHackBlock().toEpochMilli() - now.toEpochMilli());
            }
            if (userService.matchPasswords(userFromClient, userFromDb)) {
                if (userFromDb.getHackAttempts() != null && userFromDb.getHackAttempts() > 0) {
                    userService.resetHackAttempts(userFromDb);
                    userService.update(userFromDb);
                }
                return createToken(userFromDb);
            } else {
                userService.registerHackAttempt(userFromDb);
            }
        }
        return new Token();
    }

    @Transactional
    @GetMapping("/refresh")
    public Token refresh(@RequestHeader("auth_token") String token) {
        Optional<User> out = jwtProvider.verifyToken(token);
        return out.isPresent() ? createToken(out.get()) : new Token();
    }

    private boolean isHackBlocked(User userFromDb, Instant now) {
        return userFromDb.getHackBlock() != null && now.isBefore(userFromDb.getHackBlock());
    }

    private Token createToken(User user) {
        Token out = new Token();
        out.setData(jwtProvider.generateToken(user));
        return out;
    }

    public static boolean hasPermissions(List<String> permissions) {
        List<String> authorities = new ArrayList<>();
        SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()
                .forEach(a -> authorities.add(a.getAuthority()));

        return authorities.containsAll(permissions);
    }
}
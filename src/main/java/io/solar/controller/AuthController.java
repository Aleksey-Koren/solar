package io.solar.controller;


import io.solar.dto.UserDTO;
import io.solar.security.JwtProvider;
import io.solar.dto.Register;
import io.solar.dto.Token;
import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.utils.BlockedToken;
import io.solar.utils.db.Transaction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class AuthController {

    private UserService userService;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Register register(@RequestBody UserDTO userDTO) {
        User user = userDTO.userFromDTO();
        User userFromDb = userService.findByLogin(user.getLogin());
        if (userFromDb != null) {
            return new Register(false, "", "User with this login already exists");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userService.register(user);
        Token token = createToken(user);
        return new Register(true, token.getData(), "");
    }

    @PostMapping("/login")
    public Token login(@RequestBody UserDTO userDTO) {
        User user = userDTO.userFromDTO();
        User userFromDb = userService.findByLogin(user.getLogin());
        if (userFromDb != null) {
            Instant now = Instant.now();
            if (isHackBlocked(userFromDb, now)) {
                return new BlockedToken(userFromDb.getHackBlock().toEpochMilli() - now.toEpochMilli());
            }
            if (matchPasswords(user, userFromDb)) {
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

    @GetMapping("/refresh")
    public Token refresh(@RequestHeader("auth_token") String token) {
        Optional<User> out = jwtProvider.verifyToken(token);
        return out.isPresent() ? createToken(out.get()) : new Token();
    }

    private boolean isHackBlocked(User userFromDb, Instant now) {
        return userFromDb.getHackBlock() != null && now.isBefore(userFromDb.getHackBlock());
    }

    private boolean matchPasswords(User user, User userFromDb) {
        return passwordEncoder.matches(user.getPassword(), userFromDb.getPassword());
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

    public static boolean userCan(User user, String permission, Transaction transaction) {
//        if(user == null) {
//            return false;
//        }
//        Map<String, Permission> permissions = user.getPermissions();
//        if (permissions == null) {
//            Query query = transaction.query("select permission.id, permission.user_id, permission.permission_type, permission_type.title" +
//                    " from permission" +
//                    " inner join permission_type on permission.permission_type = permission_type.id" +
//                    " where user_id = :userId");
//            query.setLong("userId", user.getId());
//            user.setPermissions(query.executeQuery(new PermissionMapper()).stream().collect(Collectors.toMap(Permission::getTitle, p -> p)));
//            permissions = user.getPermissions();
//        }
//        return permissions.containsKey(permission);
        return true;
    }

}
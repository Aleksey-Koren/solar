package io.solar.controller;


import io.solar.security.JwtProvider;
import io.solar.dto.Register;
import io.solar.dto.Token;
import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.utils.BlockedToken;
import io.solar.utils.db.Transaction;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Instant;
import java.util.Optional;

@RestController
public class AuthController {

    private UserService userService;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("http://localhost/index.html");
    }

    @PostMapping("/api/register")
    public Register register(@RequestBody User user) {
        User userFromDb = userService.findByLogin(user.getLogin());
        if (userFromDb != null) {
            return new Register(false, "", "User with this login already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userService.register(user);
        Token token = createToken(user);
        return new Register(true, token.getData(), "");
    }

    @PostMapping("/api/login")
    public Token login(@RequestBody User user) {
        User userFromDb = userService.findByLogin(user.getLogin());
        if (userFromDb != null) {
            Instant now = Instant.now();
            if (isHackBlocked(userFromDb, now)) {
                return new BlockedToken(userFromDb.getHackBlock().toEpochMilli() - now.toEpochMilli());
            }
            if (matchPasswords(user, userFromDb)) {
                userService.resetHackAttempts(userFromDb);
                userService.update(userFromDb);
                return createToken(userFromDb);
            }else{
                userService.registerHackAttempt(userFromDb);
            }
        }
        return new Token();
    }

    @PostMapping(value = "/api/refresh")
    public Token authorise(@RequestBody Token token) {
        Optional<User> out = jwtProvider.verifyToken(token.getData());
        if(out.isEmpty()) {
            return new Token();
        } else {
            return createToken(out.get());
        }
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


//    @RequestMapping(value = "/authorise", method = "post")
//    public Token authorise(@RequestBody Token token) {
//        Optional<User> out = jwtProvider.verifyToken(token.getData());
//        if(out.isEmpty()) {
//            return new Token();
//        } else {
//            return createToken(out.get());
//        }
//    }














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

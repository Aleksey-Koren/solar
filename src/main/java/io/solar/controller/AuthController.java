package io.solar.controller;


import io.solar.config.jwt.JwtProvider;
import io.solar.dto.Register;
import io.solar.dto.Token;
import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.utils.db.Transaction;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class AuthController {

    private UserService userService;

    private JwtProvider jwtProvider;

    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("http://localhost/index.html");
    }

    @PostMapping("/api/register")
    public Register register(@RequestBody User user) {
        UserDetails userFromDb = userService.loadUserByUsername(user.getLogin());
        if (userFromDb != null) {
            return new Register(false, "", "User with this login already exists");
        }

        user = userService.registerUser(user);
        Token token = createToken(user);
        return new Register(true, token.getData(), "");
    }

    @PostMapping("/api/login")
    public Token login(@RequestBody User user) {
        User userFromDb = (User) userService.loadUserByUsername(user.getLogin());
        if (userFromDb != null) {
            if (userService.matchPasswords(user, userFromDb)) {
                return createToken(userFromDb);
            }
        }
        return new Token();
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




//    @RequestMapping(value = "/login", method = "post")
//    public Token login(@RequestBody User user, Transaction transaction) {
//        Query query = transaction.query("select * from users where login = :login ");
//        query.setString("login", user.getUsername());
//        List<User> users = query.executeQuery(new UserMapper());
//
//        if (users.size() > 0) {
//            String pass = hash(user.getPassword());
//            Instant now = Instant.now();
//            for (User userFromList : users) {
//                if(userFromList.getHackBlock() != null && now.isBefore(userFromList.getHackBlock())) {
//                    return new BlockedToken(userFromList.getHackBlock().toEpochMilli() - now.toEpochMilli());
//                }
//
//                if (pass.equals(userFromList.getPassword())) {
//                    query = transaction.query("update users set hack_attempts = 0, hack_block = '2010-01-01 00:00:00' where id = :id");
//                    query.setLong("id", userFromList.getId());
//                    query.executeUpdate();
//                    return createToken(userFromList);
//                } else {
//                    query = transaction.query("update users set" +
//                            " hack_attempts = if(hack_attempts is null, 1, hack_attempts + 1)," +
//                            " hack_block = if(hack_attempts < 4, '2010-01-01', " + DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
//                            .withZone(ZoneId.of("UTC")).format(now) + " + 500) where id = :id");
//                    query.setLong("id", userFromList.getId());
//                    query.executeUpdate();
//                }
//            }
//
//        }
//        return new Token();
//    }

//    private String hash(String password) {
//        String salt = System.getenv("solar_salt");
//        if (salt == null || "".equals(salt)) {
//            throw new RuntimeException("Can't register user, because no salt for pass");
//        }
//        MessageDigest md;
//        try {
//            md = MessageDigest.getInstance("MD5");
//            md.update((password + salt).getBytes());
//            byte[] digest = md.digest();
//            BigInteger bigInt = new BigInteger(1,digest);
//            StringBuilder hashtext = new StringBuilder(bigInt.toString(16));
//// Now we need to zero pad it if you actually want the full 32 chars.
//            while(hashtext.length() < 32 ){
//                hashtext.insert(0, "0");
//            }
//            return hashtext.toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

//    @RequestMapping(value = "/register", method = "post")
//    public Register register(@RequestBody User user, Transaction transaction) {
//        if (user.getLogin() == null || user.getPassword() == null || user.getLogin().length() < 3 || user.getPassword().length() < 3) {
//            return new Register(false, "", "bad request");
//        }
//        Query query = transaction.query("select * from users where login = :login");
//        query.setString("login", user.getLogin());
//        List<User> users = query.executeQuery(new UserMapper());
//        if (users.size() > 0) {
//            return new Register(false, "", "User with this login already exists");
//        }
//        query = transaction.query("insert into users (login, password, title) values (:login, :password, :title)");
//        query.setString("login", user.getLogin());
//        query.setString("password", hash(user.getPassword()));
//        query.setString("title", user.getTitle() == null ? user.getLogin() : user.getTitle());
//        query.executeUpdate();
//        Long id = query.getLastGeneratedKey(Long.class);
//        user.setId(id);
//
//        Token token = createToken(user);
//        return new Register(true, token.getData(), "");
//    }


//    private User getUser(Long id, Transaction transaction) {
//        Query query = transaction.query("select * from users where id = :id");
//        query.setLong("id", id);
//        List<User> users = query.executeQuery(new UserMapper());
//        if (users.size() == 1) {
//            return users.get(0);
//        } else {
//            return null;
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

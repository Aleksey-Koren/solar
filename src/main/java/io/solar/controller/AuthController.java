package io.solar.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.solar.dto.Register;
import io.solar.dto.Token;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.mapper.PermissionMapper;
import io.solar.mapper.UserMapper;
import io.solar.utils.BlockedToken;
import io.solar.utils.context.AuthInterface;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AuthController implements AuthInterface<User> {


    @RequestMapping(value = "/login", method = "post")
    public Token login(@RequestBody User user, Transaction transaction) {
        Query query = transaction.query("select * from users where login = :login ");
        query.setString("login", user.getLogin());
        List<User> users = query.executeQuery(new UserMapper());

        if (users.size() > 0) {
            String pass = hash(user.getPassword());
            Instant now = Instant.now();
            for (User userFromList : users) {
                if(userFromList.getHackBlock() != null && now.isBefore(userFromList.getHackBlock())) {
                    return new BlockedToken(userFromList.getHackBlock().toEpochMilli() - now.toEpochMilli());
                }

                if (pass.equals(userFromList.getPassword())) {
                    query = transaction.query("update users set hack_attempts = 0, hack_block = '2010-01-01 00:00:00' where id = :id");
                    query.setLong("id", userFromList.getId());
                    query.executeUpdate();
                    return createToken(userFromList);
                } else {
                    query = transaction.query("update users set" +
                            " hack_attempts = if(hack_attempts is null, 1, hack_attempts + 1)," +
                            " hack_block = if(hack_attempts < 4, '2010-01-01', " + DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                            .withZone(ZoneId.of("UTC")).format(now) + " + 500) where id = :id");
                    query.setLong("id", userFromList.getId());
                    query.executeUpdate();
                }
            }

        }
        return new Token();
    }

    private String hash(String password) {
        String salt = System.getenv("solar_salt");
        if (salt == null || "".equals(salt)) {
            throw new RuntimeException("Can't register user, because no salt for pass");
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update((password + salt).getBytes());
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            StringBuilder hashtext = new StringBuilder(bigInt.toString(16));
// Now we need to zero pad it if you actually want the full 32 chars.
            while(hashtext.length() < 32 ){
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/authorise", method = "post")
    public Token authorise(@RequestBody Token token) {
        Optional<User> out = this.verify(token.getData());
        if(out.isEmpty()) {
            return new Token();
        } else {
            return createToken(out.get());
        }
    }

    @RequestMapping(value = "/register", method = "post")
    public Register register(@RequestBody User user, Transaction transaction) {
        if (user.getLogin() == null || user.getPassword() == null || user.getLogin().length() < 3 || user.getPassword().length() < 3) {
            return new Register(false, "", "bad request");
        }
        Query query = transaction.query("select * from users where login = :login");
        query.setString("login", user.getLogin());
        List<User> users = query.executeQuery(new UserMapper());
        if (users.size() > 0) {
            return new Register(false, "", "User with this login already exists");
        }
        query = transaction.query("insert into users (login, password, title) values (:login, :password, :title)");
        query.setString("login", user.getLogin());
        query.setString("password", hash(user.getPassword()));
        query.setString("title", user.getTitle() == null ? user.getLogin() : user.getTitle());
        query.executeUpdate();
        Long id = query.getLastGeneratedKey(Long.class);
        user.setId(id);

        Token token = createToken(user);
        return new Register(true, token.getData(), "");
    }

    private Token createToken(User user) {
        Token out = new Token();
        Algorithm algorithm;
        try {
            String secret = System.getenv("solar_token_secret");
            if (secret == null || "".equals(secret)) {
                throw new RuntimeException("solar_token_secret system env was not defined");
            }
            algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 7))
                    .withClaim("user_id", user.getId())
                    .sign(algorithm);
            out.setData(token);
            return out;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> verify(String token) {
        try {
            Transaction transaction = Transaction.begin();
            String secret = System.getenv("solar_token_secret");
            if (secret == null || "".equals(secret)) {
                throw new RuntimeException("solar_token_secret system env was not defined");
            }
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            Claim claim = jwt.getClaim("user_id");
            Long userId = claim.as(Long.class);

            return Optional.ofNullable(getUser(userId, transaction));
        } catch (JWTVerificationException exception){
            return Optional.empty();
            //Invalid signature/claims
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private User getUser(Long id, Transaction transaction) {
        Query query = transaction.query("select * from users where id = :id");
        query.setLong("id", id);
        List<User> users = query.executeQuery(new UserMapper());
        if (users.size() == 1) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public static boolean userCan(User user, String permission, Transaction transaction) {
        if(user == null) {
            return false;
        }
        Map<String, Permission> permissions = user.getPermissions();
        if (permissions == null) {
            Query query = transaction.query("select permission.id, permission.user_id, permission.permission_type, permission_type.title" +
                    " from permission" +
                    " inner join permission_type on permission.permission_type = permission_type.id" +
                    " where user_id = :userId");
            query.setLong("userId", user.getId());
            user.setPermissions(query.executeQuery(new PermissionMapper()).stream().collect(Collectors.toMap(Permission::getTitle, p -> p)));
            permissions = user.getPermissions();
        }
        return permissions.containsKey(permission);
    }

}

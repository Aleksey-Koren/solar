package io.solar.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtProvider {

    @Value("${app.jwt_secret}")
    private String SECRET;

    @Value("${app.token_lifetime_min}")
    private Long TOKEN_LIFETIME_MIN;

    private UserService userService;

    @Autowired
    public JwtProvider(UserService userService) {
        this.userService = userService;
    }

    public String generateToken(User user) {
        Algorithm algorithm;
        try {
            String secret = SECRET;
            if (secret == null || "".equals(secret)) {
                throw new RuntimeException("solar_token_secret system env was not defined");
            }
            algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(new Date(Instant.now().toEpochMilli()).getTime() + TOKEN_LIFETIME_MIN * 60 * 1000))
                    .withClaim("user_id", user.getId())
                    .sign(algorithm);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> verifyToken(String token) {
        try {
            if (SECRET == null || "".equals(SECRET)) {
                throw new RuntimeException("solar_token_secret system env was not defined");
            }
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Long userId = jwt.getClaim("user_id").asLong();
            Optional<User> user = userService.findById(userId);
            return user;
        } catch (JWTVerificationException exception){
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public boolean hasTooShortExpiration(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(SECRET);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("auth0")
                .build();

        DecodedJWT jwt = null;
        try {
            jwt = verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new RuntimeException(e);
        }
        Instant expiration = jwt.getClaim("exp").asDate().toInstant();
        return expiration.toEpochMilli() - Instant.now().toEpochMilli() < 1000 * 60 * 60 * 5;
    }
}

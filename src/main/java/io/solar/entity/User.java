package io.solar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet")
    private Planet planet;
    private Instant hackBlock;
    private Integer hackAttempts;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;


    public static UserDetails retrieveUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(),
                true, true, true, true,
                user.getPermissions().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getTitle()))
                        .collect(toSet())
        );
    }
}
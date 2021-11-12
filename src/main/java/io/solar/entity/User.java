package io.solar.entity;


import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;
    @ManyToOne
    @JoinColumn(name = "planet")
    private Planet planet;
    private Instant hackBlock;
    private Integer hackAttempts;
    //TODO finish here, when permissions table will be ready

    @ManyToMany (fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable (name = "user_permission_type",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_type_id"))
    private Set<PermissionType> permissionTypes;


    public static UserDetails retrieveUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), user.getPassword(),
                true, true, true, true,
                user.getPermissionTypes().stream()
                        .map(permission -> new SimpleGrantedAuthority(permission.getTitle()))
                        .collect(toSet())
        );
    }
}

package io.solar.entity;

import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String title;

    @EqualsAndHashCode.Include
    private String login;

    private String email;
    private String password;
    private Long money;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    private BasicObject location;

    private Instant hackBlock;
    private Integer hackAttempts;
    private String avatar;
    private Integer emailNotifications;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "users")
    @ToString.Exclude
    private List<Room> rooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<UserRoom> userRooms;


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
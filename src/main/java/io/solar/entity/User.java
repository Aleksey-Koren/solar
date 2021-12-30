package io.solar.entity;

import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String login;
    private String password;
    private Long money;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    private BasicObject location;

    private Instant hackBlock;
    private Integer hackAttempts;
    private String avatar;
    private Short emailNotifications;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    @ManyToMany
    @JoinTable(
            name = "users_rooms",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private List<Room> rooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
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

    public List<MessageType> getMessageTypesToEmail() {
        return Arrays.stream(MessageType.values())
                .filter(s -> (this.emailNotifications & s.getIndex()) == s.getIndex())
                .collect(toList());
    }
}
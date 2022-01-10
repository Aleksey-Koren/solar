package io.solar.entity.messenger;

import io.solar.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@IdClass(UserRoom.UserRoomPK.class)
@Table(name = "users_rooms")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoom {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "subscribed_at")
    private Instant subscribedAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    public UserRoom(User user, Room room) {
        this.user = user;
        this.room = room;
        this.subscribedAt = Instant.now();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserRoomPK implements Serializable {
        private User user;
        private Room room;
    }
}
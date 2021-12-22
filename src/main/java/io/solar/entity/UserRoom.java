package io.solar.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Data
@IdClass(UserRoomPK.class)
@Table(name = "users_rooms")
public class UserRoom {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "subscribed_at")
    private Instant subscribedAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UserRoomPK implements Serializable {
    private User user;
    private Room room;
}

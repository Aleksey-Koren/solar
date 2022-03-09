package io.solar.listener;

import io.solar.entity.messenger.UserRoom;

import javax.persistence.PrePersist;
import java.time.Instant;

public class UserRoomListener {

    @PrePersist
    public void prePersist(UserRoom userRoom) {
        Instant now = Instant.now();
        userRoom.setSubscribedAt(now);
        userRoom.setLastSeenAt(now);
    }
}
package io.solar.repository.messenger;

import io.solar.entity.messenger.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, UserRoom.UserRoomPK>, JpaSpecificationExecutor<UserRoom> {


    @Query(value = "UPDATE users_rooms " +
            "SET last_seen_at =:lastSeenAt, " +
            "    subscribed_at =:subscribedAt " +
            "WHERE user_id =:userId AND room_id =:roomId", nativeQuery = true)
    void updateNative(@Param("userId")Long userId, @Param("roomId")Long roomId,
                     @Param("subscribedAt") Instant subscribedAt, @Param("lastSeenAt") Instant lastSeenAt);
}
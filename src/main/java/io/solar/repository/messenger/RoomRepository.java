package io.solar.repository.messenger;

import io.solar.dto.RoomDto;
import io.solar.dto.RoomDtoImpl;
import io.solar.entity.messenger.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(value = "WITH inner_table as (SELECT r.id as room_id, count(r.id) as unread_message " +
            "FROM users_rooms ur" +
            "    LEFT JOIN rooms r on r.id = ur.room_id" +
            "    LEFT JOIN messages m on r.id = m.room_id " +
            "WHERE ((m.created_at >= ur.last_seen_at) AND ur.user_id = :user_id) " +
            "group by r.id)" +
            "SELECT users_rooms.room_id as id, r2.title as title, inner_table.unread_message as amount " +
            "FROM users_rooms " +
            "    JOIN rooms r2 on users_rooms.room_id = r2.id " +
            "    LEFT JOIN inner_table ON users_rooms.room_id = inner_table.room_id " +
            "WHERE users_rooms.user_id = :user_id", nativeQuery = true)
    List<RoomDto> findAllUserRoomsWithUnreadMessages(@Param("user_id") Long userId);

}

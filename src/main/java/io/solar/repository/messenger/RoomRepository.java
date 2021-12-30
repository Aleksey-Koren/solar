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
/*
    SELECT rooms.id, rooms.title, count(messages.id) as amount
    from rooms
    inner join users_rooms on user_rooms.room_id = rooms.id
    left join messages on users_rooms.room_id = messages.room_id and messages.sender_id <> users_rooms.user_id and message.created_at > users_rooms.last_seen_at
    where user_rooms.user_id = 2
    group by rooms.id, rooms.title*/


    @Query(value = "WITH inner_table as (SELECT rooms.id as room_id, count(rooms.id) as unread_message " +
            "FROM users_rooms " +
            "    LEFT JOIN rooms on rooms.id = users_rooms.room_id" +
            "    LEFT JOIN messages on rooms.id = messages.room_id " +
            "WHERE ((messages.created_at >= users_rooms.last_seen_at) AND users_rooms.user_id = :user_id) " +
            "group by rooms.id)" +


            "SELECT users_rooms.room_id as id, rooms.title as title, inner_table.unread_message as amount " +
            "FROM users_rooms " +
            "    JOIN rooms on users_rooms.room_id = rooms.id " +
            "    LEFT JOIN inner_table ON users_rooms.room_id = inner_table.room_id " +
            "WHERE users_rooms.user_id = :user_id", nativeQuery = true)
    List<RoomDto> findAllUserRoomsWithUnreadMessages(@Param("user_id") Long userId);
}
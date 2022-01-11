package io.solar.repository.messenger;

import io.solar.dto.messenger.RoomDto;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
/*
    SELECT rooms.id, rooms.title, count(messages.id) as amount
    from rooms
    inner join users_rooms on user_rooms.room_id = rooms.id
    left join messages on users_rooms.room_id = messages.room_id and messages.sender_id <> users_rooms.user_id and message.created_at > users_rooms.last_seen_at
    where user_rooms.user_id = 2
    group by rooms.id, rooms.title*/


    //использовать запрос сверху
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


    //перепишите на criteria builder, потому что roomType может не передаваться
    @Query(value = "select not_my_rooms.room_id as id, rooms.title as title " +
            "from rooms " +
            "     inner join users_rooms my_rooms on my_rooms.room_id = rooms.id and my_rooms.user_id = :user_id " +
            "     inner join users_rooms not_my_rooms on my_rooms.room_id = not_my_rooms.room_id " +
            "         and not_my_rooms.user_id != :user_id " +
            "     inner join users u on not_my_rooms.user_id = u.id " +
            "WHERE (:room_type is null or rooms.type = :room_type) AND u.title like :title", nativeQuery = true)
    List<RoomDto> findAllRoomsBySearch(@Param("user_id") Long userId,
                                       @Param("room_type") String roomType,
                                       @Param("title") String title);


    @Query(value = "SELECT rooms.id AS room_id, count(room_id) as count " +
            "FROM rooms " +
            "    INNER JOIN users_rooms ur ON rooms.id = ur.room_id and type = 'PRIVATE' and (user_id =: user1Id or user_id =: user2Id) " +
            "GROUP BY (room_id) " +
            "HAVING count > 1;", nativeQuery = true)
    List<RoomDto> findPrivateRoomWithTwoUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
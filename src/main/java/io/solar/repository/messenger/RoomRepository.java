package io.solar.repository.messenger;

import io.solar.dto.messenger.RoomDto;
import io.solar.entity.messenger.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Query(value = "SELECT rooms.id as id, rooms.title as title, count(messages.id) as amount " +
            "from rooms " +
            "         inner join users_rooms on users_rooms.room_id = rooms.id " +
            "         left join messages on users_rooms.room_id = messages.room_id " +
            "            and messages.sender_id <> users_rooms.user_id " +
            "            and messages.created_at > users_rooms.last_seen_at " +
            "where users_rooms.user_id = :user_id " +
            "group by rooms.id, rooms.title", nativeQuery = true)
    List<RoomDto> findAllUserRoomsWithUnreadMessages(@Param("user_id") Long userId);


    @Query(value = "SELECT rooms.id AS room_id, count(room_id) as count " +
            "FROM rooms " +
            "    INNER JOIN users_rooms ur ON rooms.id = ur.room_id and type = 'PRIVATE' and (user_id = :user1Id or user_id = :user2Id) " +
            "GROUP BY (room_id) " +
            "HAVING count > 1;", nativeQuery = true)
    List<RoomDto> findPrivateRoomWithTwoUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
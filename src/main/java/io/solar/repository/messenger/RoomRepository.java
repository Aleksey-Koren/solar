package io.solar.repository.messenger;

import io.solar.dto.messenger.RoomDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    //читай про having
    //join на users не нужен, данные есть в таблице user_rooms
    @Query(value = "SELECT g1.room_id as id, count FROM " +
            "          (SELECT rooms.id AS room_id, count(rooms.id) AS count FROM rooms " +
            "              INNER JOIN users_rooms ur ON rooms.id = ur.room_id " +
            "                  INNER JOIN users ON ur.user_id = users.id " +
            "          WHERE (users.id =:user1Id OR users.id =:user2Id) AND type = 'PRIVATE' " +
            "          GROUP BY rooms.id)g1 " +
            "       WHERE count > 1", nativeQuery = true)
    List<RoomDto> findPrivateRoomWithTwoUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
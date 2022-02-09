package io.solar.repository.messenger;

import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long>, JpaSpecificationExecutor<UserRoom> {

    Optional<UserRoom> findByUserAndRoom(User user, Room room);
}
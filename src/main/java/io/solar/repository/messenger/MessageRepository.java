package io.solar.repository.messenger;

import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(Room room, Instant subscribedAt);
}
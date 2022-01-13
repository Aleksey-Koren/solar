package io.solar.repository.messenger;

import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findByRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(Room room, Instant subscribedAt, Pageable pageable);

    void deleteAllByRoom(Room room);
}
package io.solar.repository;

import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.UserRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class UserRoomRepositoryTest {

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Test
    @Transactional
    @Commit
    void save_shouldUpdateExistingEntity() {
        UserRoom userRoom = userRoomRepository.getById(1L);
        Instant lastSeenAtBeforeUpdate = userRoom.getLastSeenAt();
        Instant now = Instant.now();
        userRoom.setLastSeenAt(now);
        userRoomRepository.save(userRoom);
        UserRoom byId = userRoomRepository.getById(1L);
        assertEquals(now, byId.getLastSeenAt());
        assertNotEquals(lastSeenAtBeforeUpdate, byId.getLastSeenAt());
    }

    @Test
    void delete_shouldWorkCorrectly() {
        UserRoom userRoom = userRoomRepository.getById(1L);
        userRoomRepository.delete(userRoom);
        assertFalse(userRoomRepository.findById(1L).isPresent());
    }
}
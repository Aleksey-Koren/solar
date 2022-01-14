package io.solar.service;

import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;

    public void updateNative(UserRoom userRoom) {
        userRoomRepository.updateNative(userRoom.getUser().getId(),
                userRoom.getRoom().getId(),
                userRoom.getSubscribedAt(),
                userRoom.getLastSeenAt());
    }
}

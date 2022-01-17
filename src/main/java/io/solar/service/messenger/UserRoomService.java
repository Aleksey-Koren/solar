package io.solar.service.messenger;

import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
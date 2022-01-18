package io.solar.service.messenger;

import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;

    public Optional<UserRoom> findById(UserRoom.UserRoomPK userRoomPK) {
        return userRoomRepository.findById(userRoomPK);
    }

    public void updateNative(UserRoom userRoom) {
        userRoomRepository.updateNative(userRoom.getUser().getId(),
                userRoom.getRoom().getId(),
                userRoom.getSubscribedAt(),
                userRoom.getLastSeenAt());
    }
}
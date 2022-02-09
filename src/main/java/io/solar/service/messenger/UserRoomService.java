package io.solar.service.messenger;

import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;

    public UserRoom save(UserRoom userRoom) {
        return userRoomRepository.save(userRoom);
    }

    public Optional<UserRoom> findById(Long id) {
        return userRoomRepository.findById(id);
    }

    public UserRoom getById(Long id) {
        return userRoomRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with id = %d in database", UserRoom.class.getSimpleName(), id)
                ));
    }

    public Optional<UserRoom> findByUserAndRoom(User user, Room room) {
        return userRoomRepository.findByUserAndRoom(user, room);
    }

    public UserRoom getByUserAndRoom(User user, Room room) {
        return userRoomRepository.findByUserAndRoom(user, room).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s object with user_id = %d and room_id = %d"
                                , UserRoom.class.getSimpleName(), user.getId(), room.getId())
                ));
    }

    public void delete(UserRoom userRoom) {
        userRoomRepository.delete(userRoom);
    }
}
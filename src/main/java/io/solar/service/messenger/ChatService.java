package io.solar.service.messenger;

import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.MessageDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.repository.UserRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.messenger.UserRoomRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final RoomMapper roomMapper;
    private final UserRepository userRepository;

    public Page<MessageDto> getMessageHistory(Long roomId, User user, Pageable pageable) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                , "There is no room with such id = " + roomId + " . Can't fetch message history"));

        UserRoom userRoom = userRoomRepository.findById(new UserRoom.UserRoomPK(user, room))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                        , String.format("User with id = %d isn't subscribed on room id = %d", user.getId(), roomId)));

        return messageRepository
                .findByRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(room, userRoom.getSubscribedAt(), pageable)
                .map(messageMapper::toDto);
    }

    public List<RoomDtoImpl> getUserRooms(Long userId) {

        return roomRepository.findAllUserRoomsWithUnreadMessages(userId)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
    }

    public void inviteUserToRoom(Long inviterId, Long invitedId, Long roomId) {

        User inviter = userRepository.findById(inviterId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", inviterId))
        );
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no room with id = %d in database", roomId))
        );

        boolean inviterIsInRoom = userRoomRepository.existsById(new UserRoom.UserRoomPK(inviter, room));

        if (!inviterIsInRoom) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User id = %d is not in room id = %d. He can't invite anybody to this room", inviterId, roomId));
        }

        if (RoomType.SYSTEM.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Room with id = %d is SYSTEM. It is impossible to invite somebody to this room", room.getId()));
        }

        User invited = userRepository.findById(invitedId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", inviterId))
        );

        addUserToRoom(invited, room);
    }

    private void addUserToRoom(User user, Room room) {
        UserRoom userRoom = new UserRoom(user, room);
        userRoomRepository.save(userRoom);
    }

    public boolean createPrivateRoom(CreateRoomDto dto, User owner) {
        if (dto.getUserId().size() != 1) {
            throw new ServiceException(String
                    .format("Private room. userId size must be exactly 1. userId size is not 1. It is %d", dto.getUserId().size()));
        }

        Room room = new Room();
        room.setOwner(owner);
        room.setCreatedAt(Instant.now());
        room.setType(RoomType.PRIVATE);
        roomRepository.save(room);
        return inviteToPrivateRoom(room, owner, dto.getUserId().get(0));
    }

    private boolean inviteToPrivateRoom(Room room, User owner, Long interlocutorId) {
        User interlocutor = userRepository.findById(interlocutorId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", interlocutorId)));
        //TODO........
        return false;
    }
}
package io.solar.service.messenger;

import io.solar.dto.messenger.*;
import io.solar.entity.User;
import io.solar.entity.messenger.NotificationType;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.repository.UserRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.messenger.UserRoomRepository;
import io.solar.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final RoomMapper roomMapper;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    public List<Room> findAll(RoomSpecification roomSpecification) {
        return roomRepository.findAll(roomSpecification);
    }

    public List<RoomDtoImpl> getUserRooms(Long userId) {

        return roomRepository.findAllUserRoomsWithUnreadMessages(userId)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
    }

    public void inviteToExistingRoom(User inviter, Long invitedId, Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no room with id = %d in database", roomId))
        );

        if (RoomType.PRIVATE.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is impossible to invite somebody else to existing private room");
        }

        boolean inviterIsInRoom = userRoomRepository.existsById(new UserRoom.UserRoomPK(inviter, room));

        if (!inviterIsInRoom) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User id = %d is not in room id = %d. He can't invite anybody to this room", inviter.getId(), roomId));
        }

        if (RoomType.SYSTEM.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Room with id = %d is SYSTEM. It is impossible to invite somebody to this room", room.getId()));
        }

        User invited = userRepository.findById(invitedId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", invitedId))
        );

        addUserToRoom(invited, room);
    }

    private void addUserToRoom(User user, Room room) {
        UserRoom userRoom = new UserRoom(user, room);
        user.getUserRooms().add(userRoom);
        userRepository.save(user);
    }

    public ResponseEntity<Void> createRoom(CreateRoomDto dto, User owner) {
        if (dto.getIsPrivate()) {
            if (ifPrivateRoomAlreadyExists(dto.getUserId(), owner.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        Room room = new Room();
        room.setOwner(owner);
        room.setCreatedAt(Instant.now());
        room.setType(dto.getIsPrivate() ? RoomType.PRIVATE : RoomType.PUBLIC);
        room.setTitle(composeRoomTitle(dto, owner));
        roomRepository.save(room);
        addUserToRoom(owner, room);
        inviteToRoomAtCreation(room, dto.getUserId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private boolean ifPrivateRoomAlreadyExists(Long user1Id, Long user2Id) {
        return roomRepository.findPrivateRoomWithTwoUsers(user1Id, user2Id).size() > 0;
    }

    private String composeRoomTitle(CreateRoomDto dto, User owner) {
        return createTitlePartFromUser(owner) + ", " +
                createTitlePartFromUser(userRepository.findById(dto.getUserId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("There is no user with id = %d in database", dto.getUserId())))
                );
    }

    private String createTitlePartFromUser(User user) {
        return user.getTitle() != null ? user.getTitle() : "user[id = " + user.getId() + "]";
    }

    private void inviteToRoomAtCreation(Room room, Long interlocutorId) {
        User interlocutor = userRepository.findById(interlocutorId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", interlocutorId)));
        addUserToRoom(interlocutor, room);
        sendInviteNotification(interlocutor, room);
    }

    public void sendInviteNotification(User user, Room room) {
        simpMessagingTemplate.convertAndSendToUser("admin",
                "/notifications",
                new NotificationDto<RoomDto>(NotificationType.INVITED_TO_ROOM.toString(), roomMapper.toDto(room)));
    }
}
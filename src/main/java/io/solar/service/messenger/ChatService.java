package io.solar.service.messenger;

import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.MessageDto;
import io.solar.dto.messenger.NotificationDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.entity.User;
import io.solar.entity.messenger.*;
import io.solar.facade.messenger.WebSocketFacade;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.repository.UserRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.messenger.UserRoomRepository;
import io.solar.service.UserRoomService;
import io.solar.service.exception.ServiceException;
import io.solar.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRoomService userRoomService;
    private final WebSocketFacade webSocketFacade;

    public void inviteToExistingRoom(User inviter, Long invitedId, Long roomId) {

        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no room with id = %d in database", roomId)
                ));

        User invited = userRepository.findById(invitedId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", invitedId)
                ));

        if (RoomType.PRIVATE.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is impossible to invite somebody else to existing private room");
        }

        boolean inviterIsInRoom = userRoomRepository.existsById(new UserRoom.UserRoomPK(inviter, room));
        boolean invitedIsAlreadyInRoom = userRoomRepository.existsById(new UserRoom.UserRoomPK(invited, room));

        if (!inviterIsInRoom) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User id = %d is not in room id = %d. He can't invite anybody to this room", inviter.getId(), roomId));
        }

        if (invitedIsAlreadyInRoom) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User id = %d is already in room id = %d. He can't be invited to this room twice", invited.getId(), roomId));
        }

        if (RoomType.SYSTEM.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Room with id = %d is SYSTEM. It is impossible to invite somebody to this room", room.getId()));
        }

        addUserToRoom(invited, room);
    }

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

    public RoomDtoImpl createRoom(CreateRoomDto dto, User owner) {

        if (dto.getIsPrivate() && isPrivateRoomAlreadyExists(dto.getUserId(), owner.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such a private room already exists");
        }

        User interlocutor = userRepository.findById(dto.getUserId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no user with id = %d in database", dto.getUserId())));

        Room room = Room.builder()
                .owner(owner)
                .createdAt(Instant.now())
                .type(dto.getIsPrivate() ? RoomType.PRIVATE : RoomType.PUBLIC)
                .title(dto.getIsPrivate()
                        ? "[\"" + owner.getId() + ":%s\", \"" + dto.getUserId() + ":%s\"]"
                        : null)
                .build();

        Room savedRoom = roomRepository.save(room);
        savedRoom.setUsers(List.of(owner, interlocutor));
        addUserToRoom(owner, savedRoom);
        inviteToRoomAtCreation(savedRoom, interlocutor);
        return roomMapper.toDto(savedRoom);
    }

    public List<RoomDtoImpl> getUserRooms(Long userId) {

        List<RoomDtoImpl> roomDTO = roomRepository.findAllUserRoomsWithUnreadMessages(userId)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
        return roomDTO;
    }

    public List<Room> findAll(RoomSpecification roomSpecification) {

        return roomRepository.findAll(roomSpecification);
    }

    public void editMessage(User user, String updatedText, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find message with id = " + messageId));

        if (!user.equals(message.getSender())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "It's forbidden to change other users messages");
        }

        message.setMessage(updatedText);
        message.setEditedAt(Instant.now());
        messageRepository.saveAndFlush(message);

        sendEditedMessage(message);
    }

    private void sendEditedMessage(Message message) {
        Room room = message.getRoom();

        simpMessagingTemplate.convertAndSend(String.format("/room/%d", room.getId()), messageMapper.toDto(message));
    }

    public void deleteRoomsWithOneParticipantByUserRooms(User user) {
        user.getRooms()
                .stream()
                .filter(room -> room.getUsers().size() == 1)
                .forEach(room -> {
                    messageRepository.deleteAllByRoom(room);
                    roomRepository.delete(room);
                });
    }


    public void sendInviteNotification(User user, Room room) {
        simpMessagingTemplate.convertAndSendToUser(user.getLogin(),
                "/notifications",
                new NotificationDto<>(NotificationType.INVITED_TO_ROOM.name(), roomMapper.toDto(room)));
    }

    public  void sendChangeRoomTitleNotification(Room room) {
        room.getUsers().forEach(s -> simpMessagingTemplate.convertAndSendToUser(s.getLogin(),
                "/notifications",
                new NotificationDto<>(NotificationType.EDITED_ROOM_TITLE.name(), roomMapper.toDto(room))));
    }

    private boolean isPrivateRoomAlreadyExists(Long user1Id, Long user2Id) {
        return roomRepository.findPrivateRoomWithTwoUsers(user1Id, user2Id).size() > 0;
    }

    private void inviteToRoomAtCreation(Room room, User interlocutor) {
        addUserToRoom(interlocutor, room);
        sendInviteNotification(interlocutor, room);
    }

    private void addUserToRoom(User user, Room room) {
        UserRoom userRoom = new UserRoom(user, room);
        user.getUserRooms().add(userRoom);
        userRepository.saveAndFlush(user);
    }

    public HttpStatus updateLastSeenAt(Long roomId, User user) {
        Instant now = Instant.now();
        Optional<Room> roomOpt = roomRepository.findById(roomId);

        if (roomOpt.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        Optional<UserRoom> userRoomOpt = userRoomRepository.findById(new UserRoom.UserRoomPK(user, roomOpt.get()));
        if (userRoomOpt.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        UserRoom userRoom = userRoomOpt.get();
        userRoom.setLastSeenAt(now);
        userRoomService.updateNative(userRoom);
        return HttpStatus.OK;
    }

    public void updateRoomTitle(Long roomId, String roomTitle, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("There is no room with id = %d in database", roomId)));

        if (!room.getUsers().contains(user)) {
            throw new ServiceException("User, who tries to change room title, is not in room");
        }
        if (RoomType.PRIVATE.equals(room.getType())) {
            throw new ServiceException("It is impossible to change title in private room");
        }

        room.setTitle(roomTitle);
        roomRepository.save(room);

        webSocketFacade.sendSystemMessage(createChangeTitleSystemMessage(room, user, roomTitle));
        sendChangeRoomTitleNotification(room);
    }

    private Message createChangeTitleSystemMessage(Room room, User user, String roomTitle) {
       return Message.builder()
                .message("Room title has been changed to \"" + roomTitle + "\"")
                .messageType(MessageType.SYSTEM)
                .sender(user)
                .room(room)
                .build();
    }
}
package io.solar.service.messenger;

import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.MessageDto;
import io.solar.dto.messenger.RoomDto;
import io.solar.dto.messenger.notification.NotificationDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.NotificationType;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.RoomType;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exception.ServiceException;
import io.solar.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRoomService userRoomService;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final MessageService messageService;
    private final WebSocketService webSocketService;
    private final NotificationEngine notificationEngine;

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public Room getById(Long id) {
        return roomRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("there is no %s with id = %d in database", Room.class.getSimpleName(), id)
                ));
    }

    public List<RoomDto> findAllUserRoomsWithUnreadMessages(Long userId) {

        return roomRepository.findAllUserRoomsWithUnreadMessages(userId);
    }

    public Room createRoom(CreateRoomDto dto, User owner) {

        if (dto.getIsPrivate() && isPrivateRoomAlreadyExists(dto.getUserId(), owner.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Such a private room already exists");
        }

        User interlocutor = null;

        if(dto.getIsPrivate()) {
            interlocutor = userService.getById(dto.getUserId());
        }

        Room room = Room.builder()
                .owner(owner)
                .createdAt(Instant.now())
                .type(dto.getIsPrivate() ? RoomType.PRIVATE : RoomType.PUBLIC)
                .title(dto.getIsPrivate()
                        ? generatePrivateTitle(owner, interlocutor)
                        : dto.getTitle())
                .defaultTitle(true)
                .build();

        roomRepository.save(room);
        inviteToRoom(room, owner);

        if(dto.getIsPrivate()) {
            inviteToRoom(room, interlocutor);
        }

        return room;
    }

    public void updateTitle(Long roomId, String roomTitle, User user) {
        Room room = getById(roomId);

        if (!room.getUsers().contains(user)) {
            throw new ServiceException("User, who tries to change room title, is not in room");
        }
        if (RoomType.PRIVATE.equals(room.getType())) {
            throw new ServiceException("It is impossible to change title in private room");
        }

        room.setTitle(roomTitle);
        room.setDefaultTitle(false);
        roomRepository.save(room);

        sendChangeRoomTitleNotification(room);
    }

    public void inviteToExistingRoom(User inviter, Long invitedId, Long roomId) {

        Room room = getById(roomId);
        User invited = userService.getById(invitedId);

        if (RoomType.PRIVATE.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It is impossible to invite somebody else to existing private room");
        }

        boolean inviterIsInRoom = userRoomService.findByUserAndRoom(inviter, room).isPresent();
        boolean invitedIsAlreadyInRoom = userRoomService.findByUserAndRoom(invited, room).isPresent();

        if (invitedIsAlreadyInRoom) {
            return;
        }

        if (!inviterIsInRoom) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User id = %d is not in room id = %d. He can't invite anybody to this room", inviter.getId(), roomId));
        }

        if (RoomType.SYSTEM.equals(room.getType())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Room with id = %d is SYSTEM. It is impossible to invite somebody to this room", room.getId()));
        }

        addUserToRoom(invited, room);
        Message systemMessage = Message.builder()
                .message(String.format("%s was invited to room by %s", invited.getTitle(), inviter.getTitle()))
                .messageType(MessageType.SYSTEM)
                .sender(inviter)
                .room(room)
                .build();
        messageService.processNonChatMessage(systemMessage);
        webSocketService.sendSystemMessage(
                MessageDto.builder()
                        .message(systemMessage.getMessage())
                        .messageType(systemMessage.getMessageType().name())
                        .senderId(systemMessage.getSender().getId())
                        .roomId(systemMessage.getRoom().getId())
                        .build(),
                systemMessage.getRoom().getId()
        );

        notificationEngine.sendRoomUpdated(room, roomMapper.toDto(room));
    }

    public String generatePublicTitle(List<String> usersTitles) {
        return usersTitles.stream()
                .collect(joining("], [", "[", "]"));
    }

    public void deleteRoomsWithOneParticipantByUserRooms(User user) {
        user.getRooms()
                .stream()
                .filter(room -> room.getUsers().size() == 1)
                .forEach(this::deleteRoom);
    }

    public List<Room> findAll(RoomSpecification roomSpecification) {

        return roomRepository.findAll(roomSpecification);
    }

    public Room save(Room room) {

        return roomRepository.save(room);
    }

    public void deleteRoom(Room room) {
        messageRepository.deleteAllByRoom(room);
        roomRepository.delete(room);
    }

    public void removeUserFromRoom(Room room, User user) {
        UserRoom userRoom = userRoomService.getByUserAndRoom(user, room);
        userRoomService.delete(userRoom);
    }

    private boolean isPrivateRoomAlreadyExists(Long user1Id, Long user2Id) {
        return roomRepository.findPrivateRoomWithTwoUsers(user1Id, user2Id).size() > 0;
    }

    private void inviteToRoom(Room room, User user) {
        addUserToRoom(user, room);
        sendInviteNotification(user, room);
    }

    private void addUserToRoom(User user, Room room) {
        UserRoom userRoom = new UserRoom(user, room);
        userRoomService.save(userRoom);
    }

    private void sendInviteNotification(User user, Room room) {
        simpMessagingTemplate.convertAndSendToUser(user.getLogin(),
                "/notifications",
                new NotificationDto<>(NotificationType.INVITED_TO_ROOM.name(), roomMapper.toDto(room)));
    }

    private void sendChangeRoomTitleNotification(Room room) {
        room.getUsers().forEach(s -> simpMessagingTemplate.convertAndSendToUser(s.getLogin(),
                "/notifications",
                new NotificationDto<>(NotificationType.EDITED_ROOM_TITLE.name(), roomMapper.toDto(room))));
    }

    private String generatePrivateTitle(User user1, User user2) {
        return String.format("[\"%d:%s\",\"%d:%s\"]", user1.getId(), user1.getTitle(), user2.getId(), user2.getTitle());
    }

    public Message createChangeTitleSystemMessage(Long roomId, User user, String roomTitle) {
        return Message.builder()
                .message("Room title has been changed to \"" + roomTitle + "\"")
                .messageType(MessageType.SYSTEM)
                .sender(user)
                .room(getById(roomId))
                .build();
    }

    public Message createKickUserFromRoomMessage(Room room, User user, String kickedUserTitle) {

        return Message.builder()
                .message(kickedUserTitle + " has been kicked")
                .messageType(MessageType.SYSTEM)
                .sender(user)
                .room(room)
                .build();
    }

    public Message createLeaveUserFromRoomMessage(Room room, User user) {

        return Message.builder()
                .message(user.getTitle() + " left the room")
                .messageType(MessageType.SYSTEM)
                .sender(user)
                .room(room)
                .build();
    }
}
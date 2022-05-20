package io.solar.facade.messenger;

import io.solar.dto.UserDto;
import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.dto.messenger.SearchRoomDto;
import io.solar.dto.messenger.notification.KickUserNotificationPayload;
import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.UserMapper;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.messenger.RoomService;
import io.solar.service.messenger.UserRoomService;
import io.solar.specification.RoomSpecification;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class RoomFacade {
    private final WebSocketFacade webSocketFacade;
    private final RoomService roomService;
    private final UserRoomService userRoomService;

    private final UserService userService;
    private final NotificationEngine notificationEngine;
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;

    public List<SearchRoomDto> findAllRooms(User user, RoomFilter roomFilter) {
        roomFilter.setUserId(user.getId());

        return roomService.findAll(new RoomSpecification(roomFilter))
                .stream()
                .map(roomMapper::toSearchRoomDto)
                .toList();
    }

    public List<RoomDtoImpl> getUserRooms(Long userId) {

        return roomService.findAllUserRoomsWithUnreadMessages(userId)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
    }

    public List<UserDto> findAllByRoomId(Long roomId) {

        return roomService.getById(roomId)
                .getUsers().stream()
                .map(userMapper::toDtoWithIdAndTitle)
                .collect(toList());
    }

    public RoomDtoImpl createRoom(CreateRoomDto dto, User owner) {
        return roomMapper.toDto(roomService.createRoom(dto, owner));
    }

    public void updateTitle(Long roomId, String roomTitle, User user) {
        roomService.updateTitle(roomId, roomTitle, user);
        webSocketFacade.sendSystemMessage(roomService.createChangeTitleSystemMessage(roomId, user, roomTitle));
    }

    public HttpStatus updateLastSeenAt(Long roomId, User user) {
        Instant now = Instant.now();
        Optional<Room> roomOpt = roomService.findById(roomId);

        if (roomOpt.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }
        Optional<UserRoom> userRoomOpt = userRoomService.findByUserAndRoom(user, roomOpt.get());
        if (userRoomOpt.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        UserRoom userRoom = userRoomOpt.get();
        userRoom.setLastSeenAt(now);
        userRoomService.save(userRoom);
        return HttpStatus.OK;
    }

    public void inviteToExistingRoom(User inviter, Long invitedId, Long roomId) {
        roomService.inviteToExistingRoom(inviter, invitedId, roomId);
    }

    public void leaveFromRoom(User user, Long roomId) {
        Room room = roomService.getById(roomId);

        if (room.getUsers().size() != 1) {
            roomService.removeUserFromRoom(room, user);
            regenerateTitleIfNeeded(room);
            roomService.save(room);
            sendLeaveRoomNotifications(room.getUsers(), user);
        } else {
            roomService.deleteRoom(room);
        }
    }

    public void kickUserFromRoom(Long roomId, Long kickedUserId, User user) {
        Room room = roomService.getById(roomId);

        if (!room.getOwner().equals(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User with id = %d cannot kick users out of room with id = %d", user.getId(), room.getId())
            );
        }
        User kickedUser = userService.getById(kickedUserId);
        roomService.removeUserFromRoom(room, kickedUser);
        regenerateTitleIfNeeded(room);
        roomService.save(room);

        sendKickUserFromRoomNotification(kickedUser, room);
        webSocketFacade.sendSystemMessage(roomService.createKickUserFromRoomMessage(room, user, kickedUser.getTitle()));
    }

    private void regenerateTitleIfNeeded(Room room) {
        if (room.getDefaultTitle()) {
            List<String> usersTitles = room.getUsers().stream().map(User::getTitle).toList();

            room.setTitle(roomService.generatePublicTitle(usersTitles));
        }
    }

    private void sendLeaveRoomNotifications(List<User> roomParticipants, User departedUser) {
        roomParticipants.forEach(userInRoom ->
                notificationEngine.sendLeaveRoomNotification(userInRoom, userMapper.toDtoWithIdAndTitle(departedUser)));
    }

    private void sendKickUserFromRoomNotification(User kickedUser, Room room) {
        KickUserNotificationPayload payload = KickUserNotificationPayload.builder()
                .kickedUserId(kickedUser.getId())
                .roomId(room.getId())
                .roomTitle(room.getTitle())
                .build();

        notificationEngine.sendKickUserFromRoomNotification(room, payload);
    }
}
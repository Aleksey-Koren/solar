package io.solar.facade.messenger;

import io.solar.dto.UserDto;
import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.dto.messenger.SearchRoomDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.UserMapper;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.repository.messenger.RoomRepository;
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
    private final RoomMapper roomMapper;
    private final RoomRepository roomRepository;
    private final UserMapper userMapper;
    private final RoomService roomService;
    private final UserRoomService userRoomService;
    private final WebSocketFacade webSocketFacade;

    public List<SearchRoomDto> findAllRooms(User user, RoomFilter roomFilter) {
        roomFilter.setUserId(user.getId());

        return roomService.findAll(new RoomSpecification(roomFilter))
                .stream()
                .map(roomMapper::toSearchRoomDto)
                .toList();
    }

    public List<RoomDtoImpl> getUserRooms(Long userId) {
        return roomRepository.findAllUserRoomsWithUnreadMessages(userId)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
    }

    public List<UserDto> findAllByRoomId(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no room in database with id = " + roomId)
                ).getUsers().stream()
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
        Optional<Room> roomOpt = roomRepository.findById(roomId);

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
}
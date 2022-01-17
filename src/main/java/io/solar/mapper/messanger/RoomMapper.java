package io.solar.mapper.messanger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.solar.dto.messenger.RoomDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.dto.messenger.SearchRoomDto;
import io.solar.entity.messenger.Room;
import io.solar.entity.User;
import io.solar.entity.messenger.RoomType;
import io.solar.mapper.EntityDtoMapper;
import io.solar.mapper.UserMapper;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.UserRepository;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomMapper implements EntityDtoMapper<Room, RoomDtoImpl> {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Room toEntity(RoomDtoImpl dto) {

        return Objects.isNull(dto.getId())
                ? fillRoomFields(new Room(), dto)
                : findRoom(dto);
    }

    @Override
    public RoomDtoImpl toDto(Room entity) {

        return RoomDtoImpl.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .ownerId(entity.getOwner().getId())
                .roomType(entity.getType())
                .build();
    }

    public SearchRoomDto toSearchRoomDto(Room room) {

        return SearchRoomDto.builder()
                .id(room.getId())
                .title(room.getTitle())
                .createdAt(room.getCreatedAt())
                .ownerId(room.getOwner().getId())
                .roomType(room.getType())
                .participants(room.getUsers().stream().map(userMapper::toDtoWithIdAndTitle).toList())
                .build();
    }

    public RoomDtoImpl toDtoListFromInterface(RoomDto roomDto) {

        return RoomDtoImpl.builder()
                .id(roomDto.getId())
                .amount(roomDto.getAmount() == null ? 0 : roomDto.getAmount())
                .title(roomDto.getTitle())
                .build();
    }

    private Room findRoom(RoomDtoImpl dto) {
        Room room = roomRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Cannot find room with id = %d", dto.getId())));

        return fillRoomFields(room, dto);
    }

    private Room fillRoomFields(Room room, RoomDtoImpl dto) {
        User user = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Cannot find user with id = %d", dto.getOwnerId())));

        room.setTitle(dto.getTitle());
        room.setCreatedAt(dto.getCreatedAt());
        room.setOwner(user);
        room.setType(dto.getRoomType() != null ? dto.getRoomType() : null);
        return room;
    }
}
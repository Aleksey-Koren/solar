package io.solar.mapper;

import io.solar.dto.RoomDto;
import io.solar.entity.Room;
import io.solar.entity.User;
import io.solar.repository.RoomRepository;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RoomMapper implements EntityDtoMapper<Room, RoomDto> {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public Room toEntity(RoomDto dto) {

        return Objects.isNull(dto.getId())
                ? fillRoomFields(new Room(), dto)
                : findRoom(dto);
    }

    @Override
    public RoomDto toDto(Room entity) {

        return RoomDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .ownerId(entity.getOwner().getId())
                .build();
    }

    private Room findRoom(RoomDto dto) {
        Room room = roomRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find room with id = %d", dto.getId())));

        return fillRoomFields(room, dto);
    }

    private Room fillRoomFields(Room room, RoomDto dto) {
        User user = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find user with id = %d", dto.getOwnerId())));

        room.setTitle(dto.getTitle());
        room.setCreatedAt(dto.getCreatedAt());
        room.setOwner(user);

        return room;
    }


}

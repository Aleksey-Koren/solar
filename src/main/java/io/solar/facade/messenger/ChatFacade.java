package io.solar.facade.messenger;

import io.solar.dto.messenger.SearchRoomDto;
import io.solar.entity.User;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.service.messenger.ChatService;
import io.solar.specification.RoomSpecification;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatFacade {
    private final ChatService chatService;
    private final RoomMapper roomMapper;

    public List<SearchRoomDto> findAllRooms(User user, RoomFilter roomFilter) {
        roomFilter.setUserId(user.getId());

        return chatService.findAll(new RoomSpecification(roomFilter))
                .stream()
                .map(roomMapper::toSearchRoomDto)
                .toList();
    }
}
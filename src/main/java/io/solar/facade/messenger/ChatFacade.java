package io.solar.facade.messenger;

import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.mapper.messanger.RoomMapper;
import io.solar.service.messenger.ChatService;
import io.solar.specification.RoomSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatFacade {
    private final ChatService chatService;
    private final RoomMapper roomMapper;

    public List<RoomDtoImpl> findRoomsBySearch(RoomSpecification roomSpecification) {

        return chatService.findUserRoomsByLoginAndIsPrivate(roomSpecification)
                .stream()
                .map(roomMapper::toDto)
                .toList();
    }
}

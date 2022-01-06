package io.solar.facade.messenger;

import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.entity.User;
import io.solar.mapper.RoomMapper;
import io.solar.service.messenger.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatFacade {
    private final ChatService chatService;
    private final RoomMapper roomMapper;


    public List<RoomDtoImpl> findRoomsBySearch(User user, Boolean isPrivate, String login) {

        return chatService.findUserRoomsByLoginAndIsPrivate(user.getId(), isPrivate, login)
                .stream()
                .map(roomMapper::toDtoListFromInterface)
                .toList();
    }
}

package io.solar.service.messenger;

import io.solar.dto.MessageDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.mapper.MessageMapper;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.messenger.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRoomRepository userRoomRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public List<MessageDto> getMessageHistory(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST
                , "There is no room with such id = " + roomId + " . Can't fetch message history"));

        UserRoom userRoom = userRoomRepository.findById(new UserRoom.UserRoomPK(user, room))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST
                    , String.format("User with id = %d isn't subscribed on room id = %d",user.getId(), roomId)));

        return messageRepository.findByRoomAndCreatedAtAfterOrderByCreatedAt(room, userRoom.getSubscribedAt()).stream()
                .map(messageMapper::toDto)
                .collect(toList());
    }

}
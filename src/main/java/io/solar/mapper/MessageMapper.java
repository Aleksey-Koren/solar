package io.solar.mapper;

import io.solar.dto.MessageDto;
import io.solar.entity.messenger.Message;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MessageMapper implements EntityDtoMapper<Message, MessageDto> {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public Message toEntity(MessageDto dto) {
        Message entity;

        if(dto.getId() != null) {
            entity = messageRepository.findById(dto.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such id in database"));
        }else{
            entity = new Message();
        }
        entity.setSender(userRepository.findById(dto.getSenderId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such user id = " + dto.getSenderId() + " in database")
                    ));
        entity.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such room id = " + dto.getRoomId() + " in database")
        ));
        return entity;
    }

    @Override
    public MessageDto toDto(Message entity) {
        return   MessageDto.builder()
                .id(entity.getId())
                .senderId(entity.getSender().getId())
                .roomId(entity.getRoom().getId())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
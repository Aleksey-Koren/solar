package io.solar.mapper.messanger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.Message;
import io.solar.mapper.EntityDtoMapper;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static io.solar.entity.messenger.MessageType.CHAT;

@Service
@RequiredArgsConstructor
public class MessageMapper implements EntityDtoMapper<Message, MessageDto> {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public Message toEntity(MessageDto dto) {
        Message entity;

        if (dto.getId() != null) {
            entity = messageRepository.findById(dto.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such id in database"));
            entity.setCreatedAt(dto.getCreatedAt());
        } else {
            entity = new Message();
        }

        entity.setSender(userRepository.findById(dto.getSenderId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such user id = " + dto.getSenderId() + " in database")
        ));

        entity.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no such room id = " + dto.getRoomId() + " in database")
        ));

        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setMessageType(dto.getMessageType() != null ? MessageType.valueOf(dto.getMessageType()) : CHAT);
        entity.setEditedAt(dto.getEditedAt() == null ? entity.getEditedAt() : dto.getEditedAt());
        return entity;
    }

    @Override
    public MessageDto toDto(Message entity) {
        return MessageDto.builder()
                .id(entity.getId())
                .senderId(entity.getSender().getId())
                .roomId(entity.getRoom().getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .messageType(entity.getMessageType() != null ? entity.getMessageType().toString() : null)
                .editedAt(entity.getEditedAt())
                .build();
    }
}
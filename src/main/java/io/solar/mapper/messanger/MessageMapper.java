package io.solar.mapper.messanger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.UserService;
import io.solar.service.messenger.MessageService;
import io.solar.service.messenger.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.solar.entity.messenger.MessageType.CHAT;

@Service
@RequiredArgsConstructor
public class MessageMapper implements EntityDtoMapper<Message, MessageDto> {

    private final MessageService messageService;
    private final UserService userService;
    private final RoomService roomService;

    @Override
    public Message toEntity(MessageDto dto) {
        Message entity;

        if (dto.getId() != null) {
            entity = messageService.getById(dto.getId());
            entity.setCreatedAt(dto.getCreatedAt());
        } else {
            entity = new Message();
        }

        entity.setSender(userService.getById(dto.getSenderId()));

        entity.setRoom(roomService.getById(dto.getRoomId()));

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
                .senderTitle(entity.getSender().getTitle())
                .roomId(entity.getRoom().getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .messageType(entity.getMessageType() != null ? entity.getMessageType().toString() : null)
                .editedAt(entity.getEditedAt())
                .build();
    }
}
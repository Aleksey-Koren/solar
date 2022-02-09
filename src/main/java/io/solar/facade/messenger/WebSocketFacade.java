package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.messenger.Message;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.service.messenger.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WebSocketFacade {

    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageService messageService;

    @Transactional
    public MessageDto processMessage(MessageDto messageDto) {
        return messageMapper.toDto(messageService.create(messageMapper.toEntity(messageDto)));
    }

    public void sendSystemMessage(Message message) {
        messageService.processNonChatMessage(message);
        simpMessagingTemplate.convertAndSend("/room/" + message.getRoom().getId()
                , messageMapper.toDto(message));
    }

    public MessageDto editMessage(MessageDto message) {
        return messageMapper.toDto(messageService.editMessage(message));
    }
}
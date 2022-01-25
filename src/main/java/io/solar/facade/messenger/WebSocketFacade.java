package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.service.mail.EmailService;
import io.solar.service.messenger.MessageService;
import io.solar.service.messenger.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketFacade {

    private final MessageMapper messageMapper;
    private final WebSocketService webSocketService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void processMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);

        if (MessageType.CHAT.equals(message.getMessageType())) {
            webSocketService.processChatMessage(message);
        } else {
            webSocketService.processNonChatMessage(message);
        }

        messageDto.setCreatedAt(message.getCreatedAt());
    }

    public void sendSystemMessage(Message message) {
        webSocketService.processNonChatMessage(message);
        simpMessagingTemplate.convertAndSend("/room/" + message.getRoom().getId()
                , messageMapper.toDto(message));
    }

}
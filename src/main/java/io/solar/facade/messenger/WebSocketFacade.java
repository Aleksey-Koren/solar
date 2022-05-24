package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.messenger.Message;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.service.messenger.MessageService;
import io.solar.service.messenger.WebSocketService;
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
    private final WebSocketService webSocketService;

    @Transactional
    public MessageDto processMessage(MessageDto messageDto, String userLogin) {
        return messageMapper.toDto(
                messageService.create(
                        messageMapper.toEntity(messageDto),
                        userLogin
                )
        );
    }

    public void sendSystemMessage(Message message) {
        messageService.processNonChatMessage(message);
        webSocketService.sendSystemMessage(messageMapper.toDto(message), message.getRoom().getId());
    }

    public MessageDto editMessage(MessageDto message, String userLogin) {
        return messageMapper.toDto(
                messageService.editMessage(message, userLogin)
        );
    }
}
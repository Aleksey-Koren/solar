package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.service.mail.EmailService;
import io.solar.service.messenger.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketFacade {

    private final MessageMapper messageMapper;
    private final MessageService messageService;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public void processMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);

        if (MessageType.CHAT.equals(message.getMessageType())) {
            processChatMessage(message);
        } else {
            processNonChatMessage(message);
        }

        messageDto.setCreatedAt(message.getCreatedAt());
    }

    private void processChatMessage(Message message) {
        messageService.saveNew(message);
    }

    private void processNonChatMessage(Message message) {
        messageService.saveNew(message);

        List<User> usersInRoom = message.getRoom().getUsers();

        usersInRoom.stream()
                .filter(user -> (user.getEmailNotifications() != null
                        && (user.getEmailNotifications() & message.getMessageType().getIndex()) == message.getMessageType().getIndex()))
                .forEach(user -> emailService.sendSimpleEmail(user, message.getTitle(), message.getMessage()));
    }

    public void sendSystemMessage(Message message) {
        processNonChatMessage(message);
        messagingTemplate.convertAndSend("/room/" + message.getRoom().getId(), messageMapper.toDto(message));
    }
}
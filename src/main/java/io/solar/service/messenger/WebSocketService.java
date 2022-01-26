package io.solar.service.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final EmailService emailService;

    public void processChatMessage(Message message) {
        messageService.saveNew(message);
    }

    public void processNonChatMessage(Message message) {
        messageService.saveNew(message);

        List<User> usersInRoom = message.getRoom().getUsers();

        usersInRoom.stream()
                .filter(user -> (user.getEmailNotifications() != null
                        && (user.getEmailNotifications() & message.getMessageType().getIndex()) == message.getMessageType().getIndex()))
                .forEach(user -> emailService.sendSimpleEmail(user, message.getTitle(), message.getMessage()));
    }
}

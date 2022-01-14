package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.Room;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.repository.messenger.RoomRepository;
import io.solar.service.MessageService;
import io.solar.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketFacade {

    private final MessageMapper messageMapper;
    private final RoomRepository roomRepository;
    private final MessageService messageService;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    public void processMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);
        Instant savedMessageTime;

        if (MessageType.CHAT.equals(message.getMessageType())) {
            savedMessageTime = processChatMessage(message);
        } else {
            savedMessageTime = processNonChatMessage(message);
        }

        messageDto.setCreatedAt(savedMessageTime);
    }

    private Instant processChatMessage(Message message) {
        Message savedMessage = messageService.saveNew(message);

        return savedMessage.getCreatedAt();
    }

    private Instant processNonChatMessage(Message message) {
        Message savedMessage = messageService.saveNew(message);

        List<User> usersInRoom = message.getRoom().getUsers();

        usersInRoom.stream()
                .filter(user -> (user.getEmailNotifications() != null
                        &&(user.getEmailNotifications() & message.getMessageType().getIndex()) == message.getMessageType().getIndex()))
                .forEach(user -> emailService.sendSimpleEmail(user, message.getTitle(), message.getMessage()));

        return savedMessage.getCreatedAt();
    }

    public void sendSystemMessage(Message message) {
        processNonChatMessage(message);
        messagingTemplate.convertAndSend("/room/" + message.getRoom().getId(), messageMapper.toDto(message));
    }
}
package io.solar.facade.messenger;

import io.solar.dto.MessageDto;
import io.solar.entity.MessageType;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.repository.messenger.RoomRepository;
import io.solar.service.MessageService;
import io.solar.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketFacade {

    private final MessageMapper messageMapper;
    private final RoomRepository roomRepository;
    private final MessageService messageService;
    private final EmailService emailService;

    public void processMessage(MessageDto messageDto) {
        Message message = messageMapper.toEntity(messageDto);

        if (MessageType.CHAT.equals(message.getMessageType())) {
            processChatMessage(message);
        }else{
            List<User> usersInRoom = roomRepository.findById(message.getRoom().getId())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no room with id = %d in database"
                                    , message.getRoom().getId())))
                    .getUsers();

            processNonChatMessage(message, usersInRoom);
        }
    }

    private void processChatMessage(Message message) {
        messageService.saveNew(message);
    }

    private void processNonChatMessage(Message message, List<User> usersInRoom) {
        messageService.saveNew(message);
        usersInRoom.stream()
                .filter(s -> (s.getEmailNotifications() & message.getMessageType().getIndex()) == message.getMessageType().getIndex())
                .forEach(s -> emailService.sendSimpleEmail(s.getId(), message.getTitle(), message.getMessage()));
    }
}
package io.solar.service.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.security.PermissionEnum;
import io.solar.service.UserService;
import io.solar.service.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final UserRoomService userRoomService;
    private final EmailService emailService;
    private final UserService userService;

    public Message saveNew(Message message) {
        message.setCreatedAt(Instant.now());
        return messageRepository.save(message);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    public Message getById(Long id) {
        return messageRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("There is no %s with id = %d in database", Message.class.getSimpleName(), id)));
    }

    public Message update(Message message) {
        return messageRepository.save(message);
    }

    public Page<Message> getMessageHistory(Long roomId, User user, Pageable pageable) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND
                , "There is no room with such id = " + roomId + " . Can't fetch message history"));

        UserRoom userRoom = userRoomService.getByUserAndRoom(user, room);

        return messageRepository
                .findByRoomAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(room, userRoom.getSubscribedAt(), pageable);
    }

    @Transactional
    public Message editMessage(MessageDto messageDto, String userLogin) {
        Message message = getById(messageDto.getId());
        User user = userService.findByLogin(userLogin);

        if (message.getSender().equals(user)) {
            message.setMessage(messageDto.getMessage());
            message.setEditedAt(Instant.now());
            return messageRepository.saveAndFlush(message);
        } else {
            return message;
        }
    }

    public Message create(Message message, String userLogin) {
        User user = userService.findByLogin(userLogin);

        if (MessageType.CHAT.equals(message.getMessageType())) {
            return saveNew(message);
        } else if (isUserCanSendNonChatMessage(user)) {
            return processNonChatMessage(message);
        }
        //todo: replace null with smth
        return null;
    }


    public Message processNonChatMessage(Message message) {
        Message out = saveNew(message);

        List<User> usersInRoom = message.getRoom().getUsers();

        usersInRoom.stream()
                .filter(user -> (user.getEmailNotifications() != null
                        && (user.getEmailNotifications() & message.getMessageType().getIndex()) == message.getMessageType().getIndex()))
                .forEach(user -> emailService.sendSimpleEmail(user, message.getTitle(), message.getMessage()));
        return out;
    }

    private boolean isUserCanSendNonChatMessage(User user) {

        return user.getPermissions()
                .stream()
                .anyMatch(permission -> permission.getTitle().equals(PermissionEnum.SEND_ALL_MESSAGE_TYPES.name()));
    }
}
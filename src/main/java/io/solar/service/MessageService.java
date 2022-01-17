package io.solar.service;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.User;
import io.solar.entity.messenger.Message;
import io.solar.repository.messenger.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message saveNew(Message message) {
        message.setCreatedAt(Instant.now());
        return messageRepository.save(message);
    }

    public Message update(Message message) {
        return messageRepository.save(message);
    }

    public void editMessage(MessageDto messageDto) {
        Message message = messageRepository.findById(messageDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find message with id = " + messageDto.getId()));

        message.setMessage(messageDto.getMessage());
        message.setEditedAt(Instant.now());
        messageRepository.saveAndFlush(message);
    }
}
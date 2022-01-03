package io.solar.service;

import io.solar.entity.messenger.Message;
import io.solar.repository.messenger.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
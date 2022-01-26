package io.solar.facade.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.entity.User;
import io.solar.mapper.messanger.MessageMapper;
import io.solar.service.messenger.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageFacade {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public Page<MessageDto> getMessageHistory(Long roomId, User user, Pageable pageable) {
        return messageService.getMessageHistory(roomId, user, pageable).map(messageMapper::toDto);
    }
}

package io.solar.service.messenger;
import io.solar.dto.messenger.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

        private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendSystemMessage(MessageDto message, Long roomId) {
        simpMessagingTemplate.convertAndSend("/room/" + roomId
                , message);
    }
}
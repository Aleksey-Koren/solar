package io.solar.controller.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.facade.messenger.WebSocketFacade;
import io.solar.service.messenger.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class WebSocketController {

    private final MessageService messageService;
    private final WebSocketFacade webSocketFacade;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public MessageDto processMessage(@DestinationVariable("roomId") Long roomId, @Payload MessageDto message) {
        //todo - allow users send only "CHAT" messages
        //todo - allow users only edit "own" messages
        message.setRoomId(roomId);

        if (message.getId() != null) {
            message = webSocketFacade.editMessage(message);
        } else {
            message = webSocketFacade.processMessage(message);
        }
        return message;
    }
}
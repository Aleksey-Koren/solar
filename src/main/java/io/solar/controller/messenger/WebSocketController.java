package io.solar.controller.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.facade.messenger.WebSocketFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class WebSocketController {

    private final WebSocketFacade webSocketFacade;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public MessageDto processMessage(@DestinationVariable("roomId") Long roomId, @Payload MessageDto message, Principal principal) {
        //todo - allow users send only "CHAT" messages
        message.setRoomId(roomId);

        if (message.getId() != null) {
            message = webSocketFacade.editMessage(message, principal.getName());
        } else {
            message = webSocketFacade.processMessage(message, principal.getName());
        }
        return message;
    }
}
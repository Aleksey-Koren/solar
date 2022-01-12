package io.solar.controller.messenger;

import io.solar.dto.messenger.MessageDto;
import io.solar.facade.messenger.WebSocketFacade;
import io.solar.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final WebSocketFacade webSocketFacade;

//    @MessageMapping("/chat")
//    public void processMessage(@Payload Message chatMessage) {
//
//        Message saved = messageService.save(chatMessage);
//
//        messagingTemplate.convertAndSendToUser(
//                1,"/queue/messages",
//                chatMessage;
//    }


    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public MessageDto processMessage(@DestinationVariable("roomId") Long roomId, @Payload MessageDto message) {
        message.setRoomId(roomId);
        webSocketFacade.processMessage(message);
        return message;
    }
}
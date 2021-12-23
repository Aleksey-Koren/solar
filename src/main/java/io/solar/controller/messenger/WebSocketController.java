package io.solar.controller.messenger;

import io.solar.entity.messenger.Message;
import io.solar.service.MessageService;
import lombok.RequiredArgsConstructor;
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

//    @MessageMapping("/chat")
//    public void processMessage(@Payload Message chatMessage) {
//
//        Message saved = messageService.save(chatMessage);
//
//        messagingTemplate.convertAndSendToUser(
//                1,"/queue/messages",0
//                chatMessage;
//    }


    @MessageMapping("/roomId")
    @SendTo("/room/{id}")
    public Message processMessage(@Payload Message chatMessage) {
        System.out.println("I am in the controller!!!!!!!!!!!");

                messagingTemplate.convertAndSendToUser(
                1 + "","/queue/messages",
                chatMessage);
        return chatMessage;
    }
}

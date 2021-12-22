package io.solar.controller;

import io.solar.entity.Message;
import io.solar.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

//    @MessageMapping("/chat")
//    public void processMessage(@Payload Message chatMessage) {
//
//        Message saved = messageService.save(chatMessage);
//
//        messagingTemplate.convertAndSendToUser(
//                1,"/queue/messages",
//                chatMessage;
//    }


    @MessageMapping("/roomId")
    @SendTo("/1")
    public Message processMessage(@Payload Message chatMessage) {
        System.out.println("I am in the controller!!!!!!!!!!!");
        return chatMessage;
    }
}

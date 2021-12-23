package io.solar.controller.messenger;

import io.solar.dto.MessageDto;
import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.service.messenger.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/app/chat")
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    @GetMapping("messages/{roomId}")
    public List<MessageDto> getMessageHistory(@PathVariable("roomId") Long roomId, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return chatService.getMessageHistory(roomId, user);
    }
}

package io.solar.controller.messenger;

import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.MessageDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.dto.messenger.SearchRoomDto;
import io.solar.entity.User;
import io.solar.entity.messenger.MessageType;
import io.solar.facade.messenger.ChatFacade;
import io.solar.service.UserService;
import io.solar.service.messenger.ChatService;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;
    private final ChatFacade chatFacade;

    @GetMapping("room/{roomId}/messages")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MessageDto> getMessageHistory(@PathVariable("roomId") Long roomId
            , Principal principal
            , @PageableDefault(size = 20) Pageable pageable) {
        User user = userService.findByLogin(principal.getName());
        return chatService.getMessageHistory(roomId, user, pageable);
    }

    @GetMapping("user/room")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<RoomDtoImpl> getRooms(Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return chatService.getUserRooms(user.getId());
    }

    @PatchMapping("room/{roomId}/participants")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void inviteToRoom(@RequestBody Long invitedId,
                             @PathVariable("roomId") Long roomId,
                             Principal principal) {
        User inviter = userService.findByLogin(principal.getName());
        chatService.inviteToExistingRoom(inviter, invitedId, roomId);
    }

    @GetMapping("/room")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<SearchRoomDto> findRoomsBySearch(Principal principal, RoomFilter roomFilter) {
        User user = userService.findByLogin(principal.getName());

        return chatFacade.findAllRooms(user, roomFilter);
    }

    @PostMapping("/email")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void saveEmailNotifications(Principal principal, @RequestBody List<String> messageTypes) {
        List<MessageType> mappedMessageTypes = messageTypes.stream()
                .map(MessageType::valueOf)
                .collect(Collectors.toList());
        User user = userService.findByLogin(principal.getName());
        userService.saveEmailNotifications(user, mappedMessageTypes);
    }

    @PostMapping("/room")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> createRoom(@RequestBody CreateRoomDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return chatService.createRoom(dto, user);
    }

    @PatchMapping("/room/messages/{messageId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void editMessage(@PathVariable Long messageId,
                            @RequestBody String updatedText,
                            Principal principal) {

        User user = userService.findByLogin(principal.getName());

        chatService.editMessage(user, updatedText, messageId);
    }
}
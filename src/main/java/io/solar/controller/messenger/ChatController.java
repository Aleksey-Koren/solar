package io.solar.controller.messenger;

import io.solar.dto.MessageDto;
import io.solar.dto.RoomDtoImpl;
import io.solar.entity.User;
import io.solar.entity.messenger.MessageType;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.messenger.UserRoomRepository;
import io.solar.service.UserService;
import io.solar.service.messenger.ChatService;
import io.solar.specification.RoomSpecification;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;
    private final UserRoomRepository userRoomRepository;

    @GetMapping("messages/{roomId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MessageDto> getMessageHistory(@PathVariable("roomId") Long roomId
            , Principal principal
            , @PageableDefault(size = 2) Pageable pageable) {
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

    @GetMapping("/room")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void findRoomsByFilter(Principal principal, RoomFilter roomFilter) {

//        System.out.println(roomRepository.findAll(new RoomSpecification(roomFilter)));
        System.out.println(userRoomRepository.findAll(new RoomSpecification(roomFilter)));
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void inviteToRoom(@RequestParam Long inviterId,
                             @RequestParam Long invitedId,
                             @RequestParam Long roomId) {

        chatService.inviteUserToRoom(inviterId, invitedId, roomId);
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
}
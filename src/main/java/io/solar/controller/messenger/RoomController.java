package io.solar.controller.messenger;

import io.solar.dto.messenger.CreateRoomDto;
import io.solar.dto.messenger.RoomDtoImpl;
import io.solar.dto.messenger.SearchRoomDto;
import io.solar.entity.User;
import io.solar.facade.messenger.ChatFacade;
import io.solar.service.UserService;
import io.solar.service.messenger.ChatService;
import io.solar.specification.filter.RoomFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/chat/room")
@RequiredArgsConstructor
public class RoomController {

    private final UserService userService;
    private final ChatService chatService;
    private final ChatFacade chatFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<SearchRoomDto> findRoomsWithSpecificUser(Principal principal, RoomFilter roomFilter) {
        User user = userService.findByLogin(principal.getName());

        return chatFacade.findAllRooms(user, roomFilter);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<RoomDtoImpl> createRoom(@RequestBody CreateRoomDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.ok(chatService.createRoom(dto, user));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<RoomDtoImpl> getRoomsWithAmountUnreadMessages(Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return chatService.getUserRooms(user.getId());
    }

    @PatchMapping("/{roomId}/participants")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void inviteToRoom(@RequestBody Long invitedId,
                             @PathVariable("roomId") Long roomId,
                             Principal principal) {

        User inviter = userService.findByLogin(principal.getName());

        chatService.inviteToExistingRoom(inviter, invitedId, roomId);
    }

    @PatchMapping("/{roomId}/title")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void updateRoomTitle(@PathVariable("roomId") Long roomId, @RequestBody String roomTitle, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        chatService.updateRoomTitle(roomId, roomTitle, user);
    }

    @PutMapping("/{roomId}/lastSeenAt")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public ResponseEntity<Void> updateLastSeenAt(@PathVariable("roomId") Long roomId, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.status(chatService.updateLastSeenAt(roomId, user)).build();
    }
}

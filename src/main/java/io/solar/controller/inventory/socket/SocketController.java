package io.solar.controller.inventory.socket;

import io.solar.dto.inventory.socket.SocketControllerDto;
import io.solar.entity.User;
import io.solar.facade.inventory.socket.SocketFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/socket")
@RequiredArgsConstructor
public class SocketController {

    private final UserService userService;
    private final SocketFacade socketFacade;

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void attachToSocket(@PathVariable("id") Long socketId, SocketControllerDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        socketFacade.attachToSocket(dto, user, socketId);
    }
}
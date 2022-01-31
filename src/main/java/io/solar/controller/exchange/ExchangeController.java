package io.solar.controller.exchange;

import io.solar.dto.UserDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.entity.User;
import io.solar.facade.exchange.ExchangeFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exchange")
public class ExchangeController {

    private final ExchangeFacade exchangeFacade;

    @PostMapping("/invitation/request")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void sendInvitation(ExchangeInvitationDto dto, Principal principal) {

        exchangeFacade.sendInvitation(dto, principal.getName());
    }

    @PostMapping("/invitation/response")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void respondToInvitation(ExchangeInvitationDto dto, Principal principal) {

        exchangeFacade.respondToInvitation(dto, principal.getName());
    }
}

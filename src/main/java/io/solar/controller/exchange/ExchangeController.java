package io.solar.controller.exchange;

import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.User;
import io.solar.facade.exchange.ExchangeFacade;
import io.solar.facade.exchange.ExchangeOfferFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exchange")
public class ExchangeController {

    private final ExchangeFacade exchangeFacade;
    private final UserService userService;
    private final ExchangeOfferFacade exchangeOfferFacade;

    @PostMapping("/invitation/request")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void sendInvitation(@RequestBody ExchangeInvitationDto dto, Principal principal) {

        exchangeFacade.sendInvitation(dto, principal.getName());
    }

    @PostMapping("/invitation/response")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void respondToInvitation(@RequestBody ExchangeInvitationDto dto, Principal principal) {

        exchangeFacade.respondToInvitation(dto, principal.getName());
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void createExchange(@RequestBody ExchangeDto dto, Principal principal) {
        exchangeFacade.createExchange(dto, principal.getName());
    }

    @GetMapping
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public ResponseEntity<ExchangeDto> getUserExchange(Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.ok(exchangeFacade.getUserExchange(user));
    }

    @DeleteMapping
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void leaveExchange(Principal principal) {
        User user = userService.findByLogin(principal.getName());

        exchangeFacade.leaveFromExchange(user);
    }

    @PatchMapping("/offer")
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void updateOffer(@RequestBody ExchangeOfferDto exchangeOfferDto, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        exchangeOfferFacade.updateOffer(exchangeOfferDto, user);
    }
}

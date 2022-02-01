package io.solar.controller.exchange;

import io.solar.config.properties.NavigatorProperties;
import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.entity.messenger.NotificationType;
import io.solar.facade.exchange.ExchangeFacade;
import io.solar.service.engine.interfaces.NotificationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/exchange")
public class ExchangeController {

    private final ExchangeFacade exchangeFacade;
    private final NotificationEngine notificationEngine;
    private final NavigatorProperties navigatorProperties;

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
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void createExchange(@RequestBody ExchangeDto dto, Principal principal) {
        ExchangeDto exchangeDto = exchangeFacade.createExchange(dto, principal.getName());
        if (exchangeDto.getDistance() == null) {
            notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, exchangeDto.getFirstUser().getLogin(), exchangeDto);
            notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, exchangeDto.getSecondUser().getLogin(), exchangeDto);
        } else {
            notificationEngine.notificationToUser(NotificationType.MESSAGE_TO_SCREEN, exchangeDto.getFirstUser().getLogin(),
                    String.format("Exchange is not possible. Distance is too big. It's %f. You should get closer than %f to each other",
                            exchangeDto.getDistance(), navigatorProperties.getMaxExchangeDistance()));
        }
    }
}


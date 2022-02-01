package io.solar.facade.exchange;

import io.solar.config.properties.NavigatorProperties;
import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.UserMapper;
import io.solar.mapper.exchange.ExchangeMapper;
import io.solar.service.NavigatorService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeFacade {

    private final UserService userService;
    private final NotificationEngine notificationEngine;
    private final UserMapper userMapper;
    private final ExchangeService exchangeService;
    private final ExchangeMapper exchangeMapper;
    private final NavigatorService navigatorService;
    private final NavigatorProperties navigatorProperties;

    public void sendInvitation(ExchangeInvitationDto dto, String inviterLogin) {
        User inviter = userService.findByLogin(inviterLogin);
        User invited = userService.getById(dto.getUserId());
        notificationEngine.notificationToUser(NotificationType.EXCHANGE_INVITATION, invited, userMapper.toDtoWithIdAndTitle(inviter));
    }

    public void respondToInvitation(ExchangeInvitationDto dto, String invitedLogin) {
        User invited = userService.findByLogin(invitedLogin);
        User inviter = userService.getById(dto.getUserId());

        notificationEngine.notificationToUser(NotificationType.valueOf(dto.getNotificationType())
                , inviter, userMapper.toDtoWithIdAndTitle(invited));
    }

    @Transactional
    public ExchangeDto createExchange(ExchangeDto dto, String login) {
        User firstUser = userService.findByLogin(login);
        User secondUser = userService.getById(dto.getId());
        Exchange exchange = Exchange.builder()
                .firstUser(firstUser)
                .secondUser(secondUser)
                .firstAccepted(false)
                .secondAccepted(false)
                .build();
        ExchangeDto exchangeDto = exchangeMapper.toDto(exchange);
        double distance = navigatorService.calcDistance(firstUser.getLocation(), secondUser.getLocation());
        if (distance <= navigatorProperties.getMaxExchangeDistance()) {
            exchangeService.save(exchange);
            return exchangeDto;
        } else {
            exchangeDto.setDistance(distance);
            return exchangeDto;
        }
    }
}

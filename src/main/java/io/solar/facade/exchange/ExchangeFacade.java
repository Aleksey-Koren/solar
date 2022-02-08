package io.solar.facade.exchange;

import io.solar.config.properties.NavigatorProperties;
import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.UserMapper;
import io.solar.mapper.exchange.ExchangeMapper;
import io.solar.service.NavigatorService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.engine.interfaces.StarMapEngine;
import io.solar.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExchangeFacade {

    private final UserService userService;
    private final ExchangeService exchangeService;
    private final ExchangeEngine exchangeEngine;
    private final NotificationEngine notificationEngine;
    private final StarMapEngine starMapEngine;
    private final UserMapper userMapper;
    private final ExchangeMapper exchangeMapper;
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
        double distance = starMapEngine.calculateDistanceBetweenObjects(firstUser.getLocation(), secondUser.getLocation());
        if (distance <= navigatorProperties.getMaxExchangeDistance()) {
            exchangeService.save(exchange);
        } else {
            exchangeDto.setDistance(distance);
        }
        return exchangeDto;
    }

    public void createExchangeNotifications(ExchangeDto exchangeDto) {
        if (exchangeDto.getDistance() == null) {
            notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, exchangeDto.getFirstUser().getLogin(), exchangeDto);
            notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, exchangeDto.getSecondUser().getLogin(), exchangeDto);
        } else {
            notificationEngine.notificationToUser(NotificationType.MESSAGE_TO_SCREEN, exchangeDto.getFirstUser().getLogin(),
                    String.format("Exchange is not possible. Distance is too big. It's %f. You should get closer than %f to each other",
                            exchangeDto.getDistance(), navigatorProperties.getMaxExchangeDistance()));
        }
    }

    public ExchangeDto getUserExchange(User user) {
        Optional<Exchange> exchangeOptional = exchangeService.findByUser(user);

        if (exchangeOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The user is not in the exchange");
        }

        return exchangeMapper.toDtoWithOffers(exchangeOptional.get());
    }

    public void leaveFromExchange(User user) {
        Exchange exchange = exchangeService.getByUser(user);

        exchange.getExchangeOffers().forEach(this::returnItemToOwner);

        notificationEngine.sendLeaveExchangeNotification(exchangeEngine.retrieveAnotherExchangeUser(user, exchange));

        exchangeService.delete(exchange);
    }

    private void returnItemToOwner(ExchangeOffer offer) {

        switch (offer.getOfferType()) {
            case MONEY -> exchangeEngine.updateUserBalance(offer, 0L);
            case GOODS -> exchangeEngine.updateGoods(offer, 0L);
            case INVENTORY -> exchangeEngine.returnObjectToUser(offer);
        }
    }
}
package io.solar.facade.exchange;

import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.exchange.ExchangeOfferMapper;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exchange.ExchangeOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ExchangeOfferFacade {

    private final ExchangeOfferService exchangeOfferService;
    private final ExchangeEngine exchangeEngine;
    private final NotificationEngine notificationEngine;
    private final UserService userService;
    private final ExchangeOfferMapper exchangeOfferMapper;

    public void updateOffer(ExchangeOfferDto exchangeOfferDto, User user) {
        ExchangeOffer offer = exchangeOfferService.getById(exchangeOfferDto.getId());

        if (!offer.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user is not the owner of the offer");
        }

        switch (exchangeOfferDto.getOfferType()) {
            case INVENTORY -> exchangeEngine.returnObjectToUser(offer);
            case MONEY -> exchangeEngine.updateUserBalance(offer, exchangeOfferDto.getMoneyAmount());
            case GOODS -> exchangeEngine.updateGoods(offer, exchangeOfferDto.getProductAmount());
        }

        notificationEngine.sendOfferUpdatedNotification(retrieveAnotherExchangeUser(user, offer.getExchange()), exchangeOfferDto);
    }

    private User retrieveAnotherExchangeUser(User user, Exchange exchange) {

        return exchange.getFirstUser().equals(user)
                ? exchange.getSecondUser()
                : exchange.getFirstUser();
    }

    @Transactional
    public ExchangeOfferDto createOffer(ExchangeOfferDto exchangeOfferDto, String userLogin) {
        User offerPublisher = userService.findByLogin(userLogin);
        ExchangeOffer exchangeOffer = exchangeOfferMapper.toEntity(exchangeOfferDto);
        exchangeOffer.setUser(offerPublisher);
        switch (exchangeOffer.getOfferType()) {

            case INVENTORY -> exchangeEngine.putObject(exchangeOffer);
            case MONEY -> exchangeEngine.createMoneyOffer(exchangeOffer);
            case GOODS -> exchangeEngine.createGoodsOffer(exchangeOffer);
        }
        exchangeOfferService.save(exchangeOffer);

        ExchangeOfferDto dto = exchangeOfferMapper.toDto(exchangeOffer);
        dto.getExchange().getFirstUser().setLogin(exchangeOffer.getExchange().getFirstUser().getLogin());
        dto.getExchange().getSecondUser().setLogin(exchangeOffer.getExchange().getSecondUser().getLogin());
        return dto;
    }

    public void sendCreateOfferNotifications(ExchangeOfferDto dto) {
        notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, dto.getExchange().getFirstUser().getLogin(), dto);
        notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, dto.getExchange().getSecondUser().getLogin(), dto);
    }
}
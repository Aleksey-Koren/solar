package io.solar.facade.exchange;

import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.User;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exchange.ExchangeOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class ExchangeOfferFacade {

    private final ExchangeOfferService exchangeOfferService;
    private final ExchangeEngine exchangeEngine;
    private final NotificationEngine notificationEngine;

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

        notificationEngine.sendOfferUpdatedNotification(
                exchangeEngine.retrieveAnotherExchangeUser(user, offer.getExchange()),
                exchangeOfferDto
        );
    }

}

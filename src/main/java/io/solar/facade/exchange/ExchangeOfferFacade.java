package io.solar.facade.exchange;

import io.solar.dto.UserDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.dto.exchange.LayerTransferDto;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.exchange.ExchangeOfferMapper;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.ExchangeOfferEngine;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.exchange.ExchangeOfferService;
import io.solar.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;


@Component
@RequiredArgsConstructor
public class ExchangeOfferFacade {

    private final ExchangeOfferService exchangeOfferService;
    private final ExchangeEngine exchangeEngine;
    private final ExchangeOfferEngine exchangeOfferEngine;
    private final NotificationEngine notificationEngine;
    private final UserService userService;
    private final ExchangeOfferMapper exchangeOfferMapper;
    private final ExchangeService exchangeService;

    public void updateOffer(ExchangeOfferDto exchangeOfferDto, User user) {
        ExchangeOffer offer = exchangeOfferService.getById(exchangeOfferDto.getId());
        Exchange exchange = offer.getExchange();

        if (!offer.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The user is not the owner of the offer");
        }

        switch (exchangeOfferDto.getOfferType()) {
            case INVENTORY -> exchangeEngine.returnObjectToUser(offer);
            case MONEY -> exchangeEngine.updateUserBalance(offer, exchangeOfferDto.getMoneyAmount());
            case GOODS -> exchangeEngine.updateGoods(offer, exchangeOfferDto.getProductAmount());
        }

        exchange.setFirstAccepted(false);
        exchange.setSecondAccepted(false);
        exchangeService.save(exchange);

        notificationEngine.sendOfferUpdatedNotification(
                exchangeEngine.retrieveAnotherExchangeUser(user, exchange),
                exchangeOfferDto
        );
    }

    @Transactional
    public LayerTransferDto createOffer(ExchangeOfferDto exchangeOfferDto, String userLogin) {
        User offerPublisher = userService.findByLogin(userLogin);
        User secondUser = exchangeEngine.retrieveAnotherExchangeUser(offerPublisher,
                exchangeService.getById(exchangeOfferDto.getExchangeId()));
        ExchangeOffer exchangeOffer = exchangeOfferMapper.toEntity(exchangeOfferDto);
        exchangeOffer.setUser(offerPublisher);
        exchangeOffer.setCreatedAt(Instant.now());

        switch (exchangeOffer.getOfferType()) {
            case INVENTORY -> exchangeEngine.putObject(exchangeOffer);
            case MONEY -> exchangeOfferEngine.createMoneyOffer(exchangeOffer);
            case GOODS -> exchangeOfferEngine.createGoodsOffer(exchangeOffer);
        }

        exchangeOfferService.save(exchangeOffer);

        ExchangeOfferDto dto = exchangeOfferMapper.toDto(exchangeOffer);
        UserDto first = UserDto.builder().id(offerPublisher.getId())
                .login(offerPublisher.getLogin())
                .build();

        UserDto second = UserDto.builder()
                .id(secondUser.getId())
                .login(secondUser.getLogin())
                .build();

        return LayerTransferDto.builder().offerDto(dto)
                .firstUser(first)
                .secondUser(second)
                .build();
    }

    public void sendCreateOfferNotifications(LayerTransferDto dto) {
        notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, dto.getFirstUser().getLogin(), dto.getOfferDto());
        notificationEngine.notificationToUser(NotificationType.EXCHANGE_CREATED, dto.getSecondUser().getLogin(), dto.getOfferDto());
    }
}
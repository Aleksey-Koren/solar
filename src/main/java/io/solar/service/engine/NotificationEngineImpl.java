package io.solar.service.engine;

import io.solar.config.properties.MessengerProperties;
import io.solar.dto.UserDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.dto.messenger.NotificationDto;
import io.solar.entity.User;
import io.solar.entity.messenger.NotificationType;
import io.solar.service.engine.interfaces.NotificationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEngineImpl implements NotificationEngine {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessengerProperties messengerProperties;

    @Override
    public void notificationToUser(NotificationType type, User destinationUser, Object payload) {

        simpMessagingTemplate.convertAndSendToUser(destinationUser.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<Void>(NotificationType.MONEY_UPDATED.name(), null));
    }

    @Override
    public void sendLeaveRoomNotification(User userDestination, UserDto payload) {

        simpMessagingTemplate.convertAndSendToUser(userDestination.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.LEAVE_ROOM.name(), payload));
    }

    @Override
    public void sendInstantPurchaseNotification(User lotOwner, MarketplaceLotDto lotDto) {
        simpMessagingTemplate.convertAndSendToUser(lotOwner.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.INSTANT_PURCHASE.name(), lotDto));
    }

    @Override
    public void sendOfferUpdatedNotification(User userDestination, ExchangeOfferDto updatedOffer) {
        simpMessagingTemplate.convertAndSendToUser(userDestination.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.OFFER_UPDATED.name(), updatedOffer));
    }
}

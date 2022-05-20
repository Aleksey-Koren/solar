package io.solar.service.engine;

import io.solar.config.properties.MessengerProperties;
import io.solar.dto.UserDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.dto.messenger.notification.KickUserNotificationPayload;
import io.solar.dto.messenger.notification.NotificationDto;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.messenger.NotificationType;
import io.solar.entity.messenger.Room;
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
                new NotificationDto<Void>(NotificationType.MONEY_UPDATED.name()));
    }

    @Override
    public void notificationToUser(NotificationType type, String userName, Object payload) {
        simpMessagingTemplate.convertAndSendToUser(userName,
                messengerProperties.getNotificationDestination(),
                new NotificationDto<Void>(NotificationType.MONEY_UPDATED.name()));
    }

    @Override
    public <T> void sendToUser(NotificationType type, User destinationUser, T payload) {

        simpMessagingTemplate.convertAndSendToUser(destinationUser.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(type.name(), payload));
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

    @Override
    public void sendLeaveExchangeNotification(User userDestination) {
        simpMessagingTemplate.convertAndSendToUser(userDestination.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.LEAVE_EXCHANGE.name()));
    }

    @Override
    public void sendExchangeCompletedNotification(Exchange exchange) {
        simpMessagingTemplate.convertAndSendToUser(exchange.getFirstUser().getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.EXCHANGE_COMPLETED.name()));

        simpMessagingTemplate.convertAndSendToUser(exchange.getSecondUser().getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.EXCHANGE_COMPLETED.name()));
    }

    @Override
    public void sendCannotAttachToOrbitNotification(User userDestination) {
        simpMessagingTemplate.convertAndSendToUser(userDestination.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.CANNOT_ATTACH_TO_ORBIT.name()));
    }

    @Override
    public void sendKickUserFromRoomNotification(Room room, KickUserNotificationPayload payload) {
        room.getUsers().forEach(user -> {
            simpMessagingTemplate.convertAndSendToUser(user.getLogin(),
                    messengerProperties.getNotificationDestination(),
                    new NotificationDto<>(NotificationType.KICK_USER_FROM_ROOM.name(), payload));
        });
    }
}

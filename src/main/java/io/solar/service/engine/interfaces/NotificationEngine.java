package io.solar.service.engine.interfaces;

import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.dto.messenger.RoomDto;
import io.solar.dto.messenger.notification.KickUserNotificationPayload;
import io.solar.dto.messenger.notification.DepartedUserNotificationPayload;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.messenger.NotificationType;
import io.solar.entity.messenger.Room;

public interface NotificationEngine {

    void notificationToUser(NotificationType type, User destinationUser, Object payload);

    void notificationToUser(NotificationType type, String userName, Object payload);

    public <T> void sendToUser(NotificationType type, User destinationUser, T payload);

    void sendInstantPurchaseNotification(User lotOwner, MarketplaceLotDto lotDto);

    void sendOfferUpdatedNotification(User userDestination, ExchangeOfferDto updatedOffer);

    void sendLeaveExchangeNotification(User userDestination);

    void sendExchangeCompletedNotification(Exchange exchange);

    void sendCannotAttachToOrbitNotification(User userDestination);

    void sendKickUserFromRoomNotification(Room room, KickUserNotificationPayload payload);

    void sendRoomUpdated(Room room, RoomDto payload);

    void sendKickOrLeaveUserFromRoomNotification(Room room, DepartedUserNotificationPayload payload);

    void sendRoomDeletedNotification(Room deletedRoom);
}

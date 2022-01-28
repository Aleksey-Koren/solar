package io.solar.service.engine.interfaces;

import io.solar.dto.UserDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.User;
import io.solar.entity.messenger.NotificationType;

public interface NotificationEngine {

    void simpleNotification(NotificationType type, User user);

    void sendLeaveRoomNotification(User userDestination, UserDto payload);

    void sendInstantPurchaseNotification(User lotOwner, MarketplaceLotDto lotDto);
}

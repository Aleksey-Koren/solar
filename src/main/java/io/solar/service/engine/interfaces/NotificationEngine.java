package io.solar.service.engine.interfaces;

import io.solar.entity.User;
import io.solar.entity.messenger.NotificationType;

public interface NotificationEngine {

    void simpleNotification(NotificationType type, User user);
}

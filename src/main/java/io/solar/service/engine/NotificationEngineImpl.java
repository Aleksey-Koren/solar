package io.solar.service.engine;

import io.solar.config.properties.MessengerProperties;
import io.solar.dto.UserDto;
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
    public void simpleNotification(NotificationType type, User user) {

        simpMessagingTemplate.convertAndSendToUser(user.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<Void>(NotificationType.MONEY_UPDATED.name(), null));
    }

    @Override
    public void sendLeaveRoomNotification(User userDestination, UserDto payload) {

        simpMessagingTemplate.convertAndSendToUser(userDestination.getLogin(),
                messengerProperties.getNotificationDestination(),
                new NotificationDto<>(NotificationType.LEAVE_ROOM.name(), payload));
    }
}

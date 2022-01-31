package io.solar.facade.exchange;

import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeInvitationDto;
import io.solar.entity.User;
import io.solar.entity.messenger.NotificationType;
import io.solar.mapper.UserMapper;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.NotificationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeFacade {

    private final UserService userService;
    private final NotificationEngine notificationEngine;
    private final UserMapper userMapper;

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

    public void createExchange(ExchangeDto dto, String userName) {
    }
}

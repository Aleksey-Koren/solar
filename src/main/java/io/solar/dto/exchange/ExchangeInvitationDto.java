package io.solar.dto.exchange;

import io.solar.entity.messenger.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeInvitationDto {

    private Long userId;
    private String notificationType;
}

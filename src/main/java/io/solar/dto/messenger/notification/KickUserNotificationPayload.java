package io.solar.dto.messenger.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KickUserNotificationPayload {
    private Long kickedUserId;
    private Long roomId;
    private String roomTitle;
}

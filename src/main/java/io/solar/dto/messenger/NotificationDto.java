package io.solar.dto.messenger;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDto<T> {
    private String type;
    private T payload;
}

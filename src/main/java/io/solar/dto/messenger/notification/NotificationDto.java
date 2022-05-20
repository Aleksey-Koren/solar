package io.solar.dto.messenger.notification;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.management.ConstructorParameters;
import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
public class NotificationDto<T> {
    private String type;
    private T payload;

    public NotificationDto(String type) {
        this.type = type;
    }
}

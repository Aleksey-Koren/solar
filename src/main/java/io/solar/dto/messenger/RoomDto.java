package io.solar.dto.messenger;

import io.solar.entity.messenger.RoomType;

import java.time.Instant;

public interface RoomDto {
    Long getId();

    String getTitle();

    Long getAmount();

    RoomType getRoomType();

    Long getOwnerId();

    Instant getCreatedAt();
}

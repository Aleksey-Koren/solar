package io.solar.dto;

import java.time.Instant;

public interface RoomDto {
    Long getId();
    String getTitle();
    Instant getCreatedAt();
    Long getOwnerId();
    Long getAmount();
}

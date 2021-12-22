package io.solar.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageDto {

    private Long id;
    private Long senderId;
    private Long roomId;
    private String message;
    private Instant createdAt;
}
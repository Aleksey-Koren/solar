package io.solar.dto.messenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private Long id;
    private Long senderId;
    private Long roomId;
    private String title;
    private String message;
    private Instant createdAt;
    private String messageType;
}
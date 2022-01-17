package io.solar.dto.messenger;

import io.solar.entity.messenger.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RoomDtoImpl implements RoomDto{
    private Long id;
    private String title;
    private Instant createdAt;
    private Long ownerId;
    private Long amount;
    private RoomType roomType;
}
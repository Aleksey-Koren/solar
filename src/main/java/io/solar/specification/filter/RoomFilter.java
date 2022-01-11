package io.solar.specification.filter;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomFilter {
    private Long userId;
    private String roomType;
    private String title;
}

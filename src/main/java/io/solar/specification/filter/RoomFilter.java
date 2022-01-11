package io.solar.specification.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomFilter {
    private String roomType;
    private Long userId;
    private String title;
}

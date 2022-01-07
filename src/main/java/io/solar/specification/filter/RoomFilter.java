package io.solar.specification.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomFilter {
    private Boolean isPrivate;
    private String title;
    private Long userId;
}

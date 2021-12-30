package io.solar.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    CHAT(2),
    SYSTEM(4),
    NEWS(8);

    private int index;
}
package io.solar.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    CHAT(0),
    SYSTEM(2),
    NEWS(4);

    private int index;
}
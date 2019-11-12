package io.solar.utils;

import lombok.Getter;

@Getter
public class Option {
    private Long value;
    private String label;
    public Option(Long value, String label) {
        this.value = value;
        this.label = label;
    }
}

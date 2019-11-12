package io.solar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Register {
    private boolean success;
    private String token;
    private String error;
}

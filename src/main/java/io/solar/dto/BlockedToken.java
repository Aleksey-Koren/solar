package io.solar.dto;

import lombok.Getter;

@Getter
public class BlockedToken extends Token {
    private Long blocked;
    private String data = "";

    public BlockedToken(Long time) {
        blocked = time;
    }

    public void setData() {
        throw new UnsupportedOperationException();
    }
}
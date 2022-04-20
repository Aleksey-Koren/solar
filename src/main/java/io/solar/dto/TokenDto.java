package io.solar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TokenDto {

    private String data;
    private Status status;
    private Long blockedInMills;

    public TokenDto(long blocked) {
        this.blockedInMills = blocked;
        this.status = Status.BLOCKED;
    }

    public TokenDto(String data) {
        this.data = data;
        this.status = Status.VALID_CREDENTIALS;
    }

    public TokenDto() {
        this.status = Status.INVALID_CREDENTIALS;
    }

    private enum Status {
        VALID_CREDENTIALS,
        INVALID_CREDENTIALS,
        BLOCKED
    }
}
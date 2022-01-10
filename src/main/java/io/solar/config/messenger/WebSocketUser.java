package io.solar.config.messenger;

import java.security.Principal;


public record WebSocketUser(String name) implements Principal {

    @Override
    public String getName() {
        return name;
    }
}
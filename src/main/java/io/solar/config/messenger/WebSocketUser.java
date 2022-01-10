package io.solar.config.messenger;

import lombok.RequiredArgsConstructor;
import java.security.Principal;

@RequiredArgsConstructor
public class WebSocketUser implements Principal {

    private final String name;

    @Override
    public String getName() {
        return name;
    }
}

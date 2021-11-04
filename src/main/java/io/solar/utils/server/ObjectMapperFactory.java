package io.solar.utils.server;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class ObjectMapperFactory {
    public ObjectMapper create() {
        return new ObjectMapper();
    }
}

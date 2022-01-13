package io.solar.config.messenger;

import io.solar.entity.User;
import io.solar.security.JwtProvider;
import io.solar.service.exception.UserInterceptorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class UserInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message
                    .getHeaders()
                    .get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

            if (raw instanceof Map castedRaw) {
                Object token = castedRaw.get("auth_token");

                if (token == null) {
                    throw new UserInterceptorException("Cannot find auth token");
                }

                if (token instanceof ArrayList castedToken) {
                    User user = jwtProvider.verifyToken(castedToken.get(0).toString())
                            .orElseThrow(() -> new UserInterceptorException("Invalid auth token"));

                    accessor.setUser(new WebSocketUser(user.getLogin()));
                }
            }
        }
        return message;
    }
}
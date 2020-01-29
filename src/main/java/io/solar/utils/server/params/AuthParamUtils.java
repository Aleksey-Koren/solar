package io.solar.utils.server.params;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.ApplicationContext;
import io.solar.utils.context.AuthData;
import io.solar.utils.context.AuthInterface;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Optional;

public class AuthParamUtils {
    public static boolean process(Object[] args, Parameter param, int i, HttpExchange exchange, ApplicationContext context) {
        AuthData authData = param.getAnnotation(AuthData.class);
        if(authData != null) {
            List<String> tokenList = exchange.getRequestHeaders().get("auth_token");
            if(tokenList == null || tokenList.isEmpty()) {
                args[i] = null;
                return true;
            }
            String token = tokenList.get(0);
            AuthInterface auth = context.safeGet(AuthInterface.class);
            Optional optional = auth.verify(token);
            if(optional.isEmpty()) {
                args[i] = null;
            } else {
                args[i] = optional.get();
            }
            return true;
        }
        return false;
    }
}

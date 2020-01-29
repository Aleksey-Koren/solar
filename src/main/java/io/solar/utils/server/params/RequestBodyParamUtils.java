package io.solar.utils.server.params;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.ApplicationContext;
import io.solar.utils.server.ObjectMapperFactory;
import io.solar.utils.server.controller.RequestBody;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Parameter;

@Slf4j
public class RequestBodyParamUtils {
    public static boolean process(Object[] args, Parameter param, int i, HttpExchange exchange, ApplicationContext context) {

        RequestBody body = param.getAnnotation(RequestBody.class);
        if(body != null) {
            try {
                String json = new String(exchange.getRequestBody().readAllBytes());
                ObjectMapperFactory factory = context.get(ObjectMapperFactory.class);
                if(factory == null) {
                    log.error("ObjectMapperFactory was not properly configured");
                }
                args[i] = factory.create().readValue(json, param.getType());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}

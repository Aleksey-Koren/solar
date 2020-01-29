package io.solar.utils.server.params;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.UrlUtils;
import io.solar.utils.server.controller.RequestParam;

import java.lang.reflect.Parameter;

public class GetParamsParamUtils {
    public static boolean process(Object[] args, Parameter param, int i, HttpExchange exchange) {
        RequestParam requestParam = param.getAnnotation(RequestParam.class);
        if (requestParam != null) {
            String source = UrlUtils.getQuery(exchange, requestParam.value());
            if(source == null) {
                args[i] = null;
                return true;
            }
            if (param.getType().equals(String.class)) {
                args[i] = source;
            } else if (param.getType().equals(Integer.class)) {
                try {
                    args[i] = Integer.parseInt(source);
                } catch (Exception e) {
                    throw new RuntimeException("Bad request, get param " + requestParam.value() + " should be int, got " + source);
                }
            } else if (param.getType().equals(Long.class)) {
                try {
                    args[i] = Long.parseLong(source);
                } catch (Exception e) {
                    throw new RuntimeException("Bad request, get param " + requestParam.value() + " should be long, got " + source);
                }
            } else if(param.getType().equals(Boolean.class)) {
                args[i] = "true".equals(source) || "t".equals(source) || "1".equals(source);
            }
            return true;
        }
        return false;

    }
}

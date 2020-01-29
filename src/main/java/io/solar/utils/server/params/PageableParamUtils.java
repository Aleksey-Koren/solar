package io.solar.utils.server.params;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.UrlUtils;
import io.solar.utils.server.Pageable;

import java.lang.reflect.Parameter;

public class PageableParamUtils {
    public static boolean process(Object[] args, Parameter param, int i, HttpExchange exchange) {
        if(param.getType().equals(Pageable.class)) {
            String pageS = UrlUtils.getQuery(exchange, "page");
            String sizeS = UrlUtils.getQuery(exchange, "size");
            int page;
            try {
                if(null != pageS && !"".equals(pageS)) {
                    page = Integer.parseInt(pageS);
                } else {
                    page = 0;
                }
            } catch (Exception e) {
                page = 0;
            }
            int size;
            try {
                if(null != sizeS && !"".equals(sizeS)) {
                    size = Integer.parseInt(sizeS);
                } else {
                    size = 20;
                }
            } catch (Exception e) {
                size = 20;
            }
            args[i] = new Pageable(page, size);
            return true;
        }
        return false;
    }
}

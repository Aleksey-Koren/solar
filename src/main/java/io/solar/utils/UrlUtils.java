package io.solar.utils;

import com.sun.net.httpserver.HttpExchange;

public class UrlUtils {
    public static String getPath(HttpExchange exchange) {
        return exchange.getRequestURI().getPath();
    }
    public static String[] getPathArray(HttpExchange exchange) {
        String[] p = exchange.getRequestURI().getPath().split("/");
        if(p.length == 0) {
            return new String[]{"/"};
        }
        String[] out = new String[p.length - 1];
        System.arraycopy(p, 1, out, 0, p.length - 1);
        return out;
    }
    public static String getPath(HttpExchange exchange, int index) {
        String path = getPath(exchange);
        String[] parts = path.split("/");
        index = index + 1;
        return parts.length > index ? parts[index] : "";
    }

    public static String getQuery(HttpExchange exchange, String name) {
        String query = exchange.getRequestURI().getQuery();
        if(query == null || name == null) {
            return null;
        }
        if(query.contains("&")) {
            String[] p = query.split("&");
            for(String q : p) {
                String out = getSingleQuery(q, name);
                if(out != null) {
                    return out;
                }
            }
            return null;
        } else {
            return getSingleQuery(query, name);
        }
    }

    private static String getSingleQuery(String query, String name) {
        if(query.indexOf(name) == 0) {
            int eqPosition = query.indexOf("=");
            return eqPosition == name.length() ? query.substring(eqPosition + 1) : "";
        } else {
            return null;
        }
    }


}

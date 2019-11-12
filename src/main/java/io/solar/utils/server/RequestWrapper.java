package io.solar.utils.server;

import com.sun.net.httpserver.HttpExchange;

public class RequestWrapper {

    private HttpExchange exchange;

    public RequestWrapper(HttpExchange exchange) {
        this.exchange = exchange;
    }



}

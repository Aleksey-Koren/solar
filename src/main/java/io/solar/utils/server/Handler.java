package io.solar.utils.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.solar.utils.ApplicationContext;

public class Handler implements HttpHandler {

    private final String resources;
    private final String api;
    private final ApplicationContext context;

    public Handler(ApplicationContext context, String resources, String api) {
        this.resources = resources;
        this.context = context;
        this.api = api;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        new ThreadHandler(httpExchange, resources, api, context).start();
    }

}

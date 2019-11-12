package io.solar.utils;


import com.sun.net.httpserver.HttpServer;
import io.solar.utils.server.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    private final String resources;
    private final String api;

    public Server(String resources, String api) {
        this.resources = resources;
        this.api = api;
    }

    public void start(ApplicationContext context) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);
        server.createContext("/", new Handler(context, resources, api));
        server.start();
    }
}

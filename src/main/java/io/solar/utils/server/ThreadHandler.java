package io.solar.utils.server;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.ApplicationContext;
import io.solar.utils.UrlUtils;
import io.solar.utils.context.ApiBridge;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;

public class ThreadHandler extends Thread {

    private final HttpExchange exchange;
    private final String resources;
    private final String api;
    private final ApplicationContext context;

    public ThreadHandler(HttpExchange exchange, String resources, String api, ApplicationContext context) {
        this.exchange = exchange;
        this.resources = resources;
        this.api = api;
        this.context = context;
    }

    public void run() {
        OutputStream os = null;
        try {
            String[] path = UrlUtils.getPathArray(exchange);
            os = exchange.getResponseBody();
            if(api.equals(path[0])) {
                ApiBridge apiBridge = context.get(ApiBridge.class);
                ApiResponse response = apiBridge.process(exchange, path);
                long size;
                String body;
                if(response.getBody() != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(response.getBody());
                    byte[] b = json.getBytes();
                    exchange.sendResponseHeaders(response.getCode(), b.length);
                    exchange.getResponseHeaders().add("content-type", "application/json");
                    os.write(b);
                } else {
                    exchange.sendResponseHeaders(response.getCode(), 0);
                }
            } else if(resources.equals(path[0]) || "public/assets/favicon.ico".equals(path[0]) || "/".equals(path[0]) || "public/assets/1.html".equals(path[0])) {
                InputStream in;
                if("/".equals(path[0])) {
                    in = getClass().getResourceAsStream("/public/assets/1.html");
                } else {
                    in = getClass().getResourceAsStream(exchange.getRequestURI().toString());
                }
                if(in != null) {
                    exchange.getResponseHeaders().add("content-type", defineContentType(path));
                    byte[] b = in.readAllBytes();
                    exchange.sendResponseHeaders(200, b.length);
                    os.write(b);
                } else {
                    notFound(exchange, os);
                }
            } else {
                notFound(exchange, os);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String defineContentType(String[] path) {
        if(path.length == 0) {
            return "";
        }
        String l = path[path.length - 1];
        if("public/assets/favicon.ico".equals(l)) {
            return "image/x-icon";
        } else {
            int index = l.lastIndexOf(".");
            if(index == -1) {
                return "";
            }
            String ext = l.substring(index + 1);
            switch (ext) {
                case "js":
                    return "application/javascript";
                case "xml":
                case "json":
                case "javascript":
                case "pdf":
                case "postscript":
                case "zip":
                case "gzip":
                case "msword":
                    return "application/" + ext;

                case "aac":
                case "vorbis":
                    return "audio/" + ext;

                case "gif":
                case "jpeg":
                case "pjpeg":
                case "png":
                case "tiff":
                case "webp":
                    return "image/" + ext;
                case "svg":
                    return "image/svg+xml";
                case "cmd":
                case "css":
                case "csv":
                case "html":
                case "plain":
                    return "text/"  + ext;

                case "mpeg":
                case "mp4":
                case "ogg":
                case "webm":
                case "3gpp":
                case "3gpp2":
                    return "video/"  + ext;
                default:
                    return "application/octet-stream";
            }
        }
    }

    private void notFound(HttpExchange exchange, OutputStream os) throws IOException {
        String out = "not found, 404";
        exchange.sendResponseHeaders(404, out.length());
        os.write(out.getBytes());
    }

    @Override
    public String toString() {
        return "ApiBridge (api: " + api + "; resources: " + resources + ")";
    }
}


package io.solar.utils.context;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.ApplicationContext;
import io.solar.utils.server.ApiResponse;
import io.solar.utils.server.ControllerExecutable;
import io.solar.utils.server.controller.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiBridge {

    private final ApplicationContext context;
    private final Map<String, List<ControllerExecutable>> controllers;

    public ApiBridge(ApplicationContext context) {
        this.context = context;
        controllers = new HashMap<>();
    }

    public ApiResponse process(HttpExchange exchange, String[] path) {

        String[] realPath = new String[path.length - 1];
        System.arraycopy(path, 1, realPath, 0, path.length - 1);
        List<ControllerExecutable> executables = controllers.get(realPath[0]);
        String method = exchange.getRequestMethod().toLowerCase();
        if(executables != null) {
            for(ControllerExecutable executable : executables) {
                if(executable.match(realPath, method)) {
                    return executable.execute(exchange, realPath, context);
                }
            }
            return new ApiResponse("method not found", 404);
        } else {
            for(Map.Entry<String, List<ControllerExecutable>> entry : controllers.entrySet()) {
                executables = entry.getValue();
                for(ControllerExecutable executable : executables) {
                    if(executable.match(path, method)) {
                        return executable.execute(exchange, realPath, context);
                    }
                }
            }
            return new ApiResponse("controller not found", 404);
        }
    }

    public <T> void mapController(T instance, RequestMapping requestMapping) {
        String path = null;
        if(requestMapping != null) {
            path = requestMapping.value().replaceAll("(^/)|(/$)","");
        }
        Method[] methods = instance.getClass().getDeclaredMethods();
        for(Method method : methods) {
            if(!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            RequestMapping annotation = method.getDeclaredAnnotation(RequestMapping.class);
            if(annotation == null) {
                continue;
            }
            String[] pathParts = ((path == null ? "" : path + "/") + annotation.value().replaceAll("(^/)|(/$)","")).split("/");
            List<ControllerExecutable> executables = controllers.computeIfAbsent(pathParts[0], k -> new ArrayList<>());
            executables.add(new ControllerExecutable(pathParts, annotation.method(), instance, method));
        }
    }
}

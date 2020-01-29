package io.solar.utils.server.params;

import io.solar.utils.server.ApiResponse;
import io.solar.utils.server.controller.PathVariable;

import java.lang.reflect.Parameter;

public class RequestPathParamUtils {
    public static boolean process(Object[] args, Parameter param, int i, String[] pathParts, String[] path) {
        PathVariable pathVariable = param.getAnnotation(PathVariable.class);
        if(pathVariable != null) {
            String varName = "{" + pathVariable.value() + "}";
            int index = -1;
            for(int j = 0; j < pathParts.length; j++) {
                String pathPart = pathParts[j];
                if(pathPart.equals(varName)) {
                    index = j;
                    break;
                }
            }
            if(index == -1) {
                args[i] = null;
            } else {
                String source = path[index];
                if (param.getType().equals(String.class)) {
                    args[i] = source;
                } else if(param.getType().equals(Integer.class)) {
                    try {
                        args[i] = Integer.parseInt(source);
                    } catch (Exception e) {
                        throw new RuntimeException("Bad request, path param should be int, got " + source);
                    }
                } else if(param.getType().equals(Long.class)) {
                    try {
                        args[i] = Long.parseLong(source);
                    } catch (Exception e) {
                        throw new RuntimeException("Bad request, path param should be long, got " + source);
                    }
                }
            }
            return true;
        }
        return false;
    }
}

package io.solar.utils.server;

import com.sun.net.httpserver.HttpExchange;
import io.solar.utils.ApplicationContext;
import io.solar.utils.Option;
import io.solar.utils.UrlUtils;
import io.solar.utils.context.AuthData;
import io.solar.utils.context.AuthInterface;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ControllerExecutable {
    private final String[] pathParts;
    private final String httpMethod;
    private final Object controller;
    private final Method method;

    public ControllerExecutable(String[] pathParts, String httpMethod, Object controller, Method method) {
        this.pathParts = pathParts;
        this.httpMethod = httpMethod;
        this.controller = controller;
        this.method = method;
    }

    public boolean match(String[] urlParts, String httpMethod) {
        if(!this.httpMethod.equals(httpMethod) || pathParts.length != urlParts.length) {
            return false;
        }
        for(int i = 0; i < pathParts.length; i++) {
            if(!pathParts[i].equals(urlParts[i])) {
                if(!pathParts[i].startsWith("{")) {
                    return false;
                }
            }
        }
        return true;
    }

    public ApiResponse execute(HttpExchange exchange, String[] path, ApplicationContext context) {
        Transaction transaction = null;
        try {
            Parameter[] params = method.getParameters();
            Object[] args = new Object[params.length];

            for(int i = 0; i < params.length; i++) {
                Parameter param = params[i];

                if(param.getType().equals(Transaction.class)) {
                    if(transaction == null) {
                        transaction = Transaction.begin();
                    }
                    args[i] = transaction;
                    continue;
                }


                AuthData authData = param.getAnnotation(AuthData.class);
                if(authData != null) {
                    List<String> tokenList = exchange.getRequestHeaders().get("auth_token");
                    if(tokenList == null || tokenList.isEmpty()) {
                        args[i] = null;
                        continue;
                    }
                    String token = tokenList.get(0);
                    AuthInterface auth = context.safeGet(AuthInterface.class);
                    Optional optional = auth.verify(token);
                    if(optional.isEmpty()) {
                        args[i] = null;
                    } else {
                        args[i] = optional.get();
                    }
                    continue;
                }
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
                    continue;
                }
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
                                return new ApiResponse("Bad request", 400);
                            }
                        } else if(param.getType().equals(Long.class)) {
                            try {
                                args[i] = Long.parseLong(source);
                            } catch (Exception e) {
                                return new ApiResponse("Bad request", 400);
                            }
                        }
                    }
                }

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
                }
            }
            if(method.getReturnType().equals(void.class)) {
                method.invoke(controller, args);
                if (transaction != null) {
                    transaction.commit();
                }
                return new ApiResponse();
            } else {
                Object out = method.invoke(controller, args);
                if (transaction != null) {
                    transaction.commit();
                }
                return new ApiResponse(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(transaction != null) {
                transaction.rollback();
            }
            return new ApiResponse("internal server error", 500);
        }
    }

    @Override
    public String toString() {
        return httpMethod + ": " + String.join("/", pathParts);
    }
}

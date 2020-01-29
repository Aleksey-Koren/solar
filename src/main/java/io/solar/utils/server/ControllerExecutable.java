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
import io.solar.utils.server.params.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

                if(TransactionParamUtils.process(args, param, i, transaction)) {
                    if(transaction == null) {
                        transaction = (Transaction) args[i];
                    }
                    continue;
                }

                if(AuthParamUtils.process(args, param, i, exchange, context)) {
                    continue;
                }

                if(RequestBodyParamUtils.process(args, param, i, exchange, context)) {
                    continue;
                }
                try {
                    if (RequestPathParamUtils.process(args, param, i, pathParts, path)) {
                        continue;
                    }
                    if(GetParamsParamUtils.process(args, param, i, exchange)) {
                        continue;
                    }
                } catch (Exception e) {
                    return new ApiResponse(e.getMessage(), 400);
                }


                if(PageableParamUtils.process(args, param, i, exchange)) {
                    continue;
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

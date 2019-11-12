package io.solar.utils;

import io.solar.controller.AuthController;
import io.solar.utils.context.ApiBridge;
import io.solar.utils.context.AuthInterface;
import io.solar.utils.context.BeanCreator;
import io.solar.utils.server.controller.Controller;
import io.solar.utils.server.controller.RequestMapping;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final Map<Class, Object> context;

    public ApplicationContext() {
        context = new HashMap<>();
        put(BeanCreator.class, new BeanCreator(this));
        put(ApplicationContext.class, this);
    }

    public <T> T get(Class<T> clazz) {
        Object object = findClass(clazz);
        return clazz.cast(object);
    }
    public <T> T safeGet(Class<T> clazz) {
        Object object = findClass(clazz);
        if(object == null) {
            return null;
        } else {
            return clazz.cast(object);
        }
    }

    public <T> void put(Class<T> clazz) {
        T instance = get(BeanCreator.class).create(clazz);
        put(clazz, instance);
    }

    public <T> void put(Class<T> clazz, T instance) {
        context.put(clazz, instance);
        RequestMapping requestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);
        Controller controller = null;
        if(requestMapping == null) {
            controller = clazz.getDeclaredAnnotation(Controller.class);
        }
        if(requestMapping != null || controller != null) {
            ApiBridge bridge = get(ApiBridge.class);
            bridge.mapController(instance, requestMapping);
        }
    }

    private <T>Object findClass(Class<T> clazz) {
        Object object = context.get(clazz);
        if(object != null) {
            return object;
        } else {
            for(Map.Entry<Class, Object> entry : context.entrySet()) {
                object = entry.getValue();
                if(clazz.isAssignableFrom(object.getClass())) {
                    return object;
                }
            }
            return null;
        }
    }
}

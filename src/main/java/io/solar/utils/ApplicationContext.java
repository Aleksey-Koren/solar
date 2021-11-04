package io.solar.utils;

import io.solar.utils.context.ApiBridge;
import io.solar.utils.context.BeanCreator;
import io.solar.utils.server.beans.Controller;
import io.solar.utils.server.controller.RequestMapping;
import io.solar.utils.server.beans.Service;
import io.solar.utils.server.controller.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@Component
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
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            Scheduled scheduled = method.getDeclaredAnnotation(Scheduled.class);
            if(scheduled != null) {
                Timer timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            method.invoke(instance);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, scheduled.interval());
            }
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

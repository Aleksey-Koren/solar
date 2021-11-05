package io.solar.utils.context;

import io.solar.utils.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BeanCreator {

    private final ApplicationContext context;

    public BeanCreator(ApplicationContext context) {
        this.context = context;
    }

    public <T> T create(Class<T> clazz) {
            Constructor<?>[] constructors = clazz.getConstructors();
            Map<Integer, List<Constructor<?>>> counter = new HashMap<>();
            int max = -1;
            for(Constructor<?> constructor : constructors) {
                int count = constructor.getParameterCount();
                List<Constructor<?>> list = counter.computeIfAbsent(count, k -> new ArrayList<>());
                list.add(constructor);
                if(count > max) {
                    max = count;
                }
            }
            while(max >= 0) {
                var list = counter.get(max);
                if(list != null) {
                    for(var constructor : list) {
                        T object = tryInstantiate(clazz, constructor);
                        if(object != null) {
                            return clazz.cast(object);
                        }
                    }
                }
                max--;
            }
            throw new RuntimeException("Could not instantiate bean of class: " + clazz.getName());
    }

    private <T> T tryInstantiate(Class<T> clazz, Constructor<?> constructor) {
        if(!Modifier.isPublic(constructor.getModifiers())) {
            return null;
        }

        try {
            Class<?>[] params = constructor.getParameterTypes();
            if(params.length == 0) {
                return clazz.cast(constructor.newInstance());
            }
            Object[] args = new Object[params.length];
            int index = 0;
            for(Class<?> paramClass : params) {
                Object arg = context.safeGet(paramClass);
                if(arg != null) {
                    args[index] = arg;
                    index++;
                } else {
                    return null;
                }
            }
            return clazz.cast(constructor.newInstance(args));

        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}

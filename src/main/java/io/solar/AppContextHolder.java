package io.solar;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AppContextHolder {

    @Getter
    private static ApplicationContext context;

    @Autowired
    public AppContextHolder(ApplicationContext applicationContext) {
        AppContextHolder.context = applicationContext;
    }

}

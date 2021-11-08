package io.solar;

import io.solar.controller.*;
import io.solar.controller.inventory.*;
import io.solar.service.ObjectService;
import io.solar.service.StarShipService;
import io.solar.utils.ApplicationContext;
import io.solar.utils.Server;
import io.solar.utils.context.ApiBridge;
import io.solar.utils.db.Pool;
import io.solar.utils.server.ObjectMapperFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.io.IOException;


@SpringBootApplication
public class Start {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) throws IOException {



        SpringApplication app = new SpringApplication(Start.class);
        app.run(args);

        Pool.init(
                "jdbc:mysql://localhost/solar?useSSL=false&serverTimezone=UTC",
                System.getenv("solar_db_user"),
                System.getenv("solar_db_pass"),
                8
        );

        ApplicationContext context =
                AppContextHolder.getContext().getBean("applicationContext", ApplicationContext.class);

        context.put(ApiBridge.class);
        context.put(ObjectService.class);
        context.put(StarShipService.class);
        context.put(PlanetController.class);
        context.put(ProductController.class);
        context.put(StationController.class);
        context.put(ObjectMapperFactory.class);
        context.put(AuthController.class);
        context.put(InventoryTypeController.class);
        context.put(InventoryItemsController.class);
        context.put(PermissionsController.class);
        context.put(UsersController.class);
        context.put(InventoryModificationsController.class);
        context.put(ObjectsController.class);
        context.put(SocketController.class);

        Server s = new Server("assets", "api");
        s.start(context);

    }
}

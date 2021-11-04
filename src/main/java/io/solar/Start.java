package io.solar;

import io.solar.controller.*;
import io.solar.controller.inventory.*;
import io.solar.entity.User;
import io.solar.service.ObjectService;
import io.solar.service.StarShipService;
import io.solar.utils.ApplicationContext;
import io.solar.utils.Server;
import io.solar.utils.context.ApiBridge;
import io.solar.utils.db.Pool;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class Start {

    public static void main(String[] args) throws IOException {

        Pool.init(
                "jdbc:mysql://localhost/solar?useSSL=false&serverTimezone=UTC",
                System.getenv("solar_db_user"),
                System.getenv("solar_db_pass"),
                8
        );



        ApplicationContext context = new ApplicationContext();
        context.put(ApiBridge.class, new ApiBridge(context));
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

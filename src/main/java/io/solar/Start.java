package io.solar;

import io.solar.controller.*;
import io.solar.controller.inventory.InventoryItemsController;
import io.solar.controller.inventory.InventoryModificationsController;
import io.solar.controller.inventory.InventoryTypeController;
import io.solar.service.StarShipService;
import io.solar.utils.ApplicationContext;
import io.solar.utils.Server;
import io.solar.utils.context.ApiBridge;
import io.solar.utils.db.Pool;
import io.solar.utils.server.ObjectMapperFactory;

import java.io.IOException;
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

        Server s = new Server("assets", "api");
        s.start(context);
    }

}

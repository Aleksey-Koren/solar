package io.solar.service.engine;

import io.solar.entity.objects.StarShip;
import io.solar.service.InitService;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class InventoryEngineImplTest {

    @Autowired
    private UserService userService;
    @Autowired
    private StarShipService starShipService;
    @Autowired
    private StarShipEngineImpl starShipEngine;
    @Autowired
    private InitService initService;

    @Test
    @Transactional
    @Commit
    void dropToSpaceList() {
        StarShip ship = starShipService.getById(userService.getById(2L).getLocation().getId());
        starShipEngine.blowUp(ship);
    }
}

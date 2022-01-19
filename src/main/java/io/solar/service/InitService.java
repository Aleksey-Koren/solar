package io.solar.service;

import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.StarShipRepository;
import io.solar.security.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UtilityService utilityService;
    private final UserService userService;
    private final StarShipRepository starShipRepository;
    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final BasicObjectRepository basicObjectRepository;

    private final String LOGIN = "admin";
    private final String PASSWORD = "admin";
    @Value("${app.admin_not_exists}")
    private String adminNotExists;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(InitService.class).defaultAdminInitialization();
    }

    @Transactional
    public void defaultAdminInitialization() {
        if (!adminNotExists.equals("yes")) {
            return;
        }
        if (!utilityService.getValue("admin_not_exists").orElse("").equals("yes")) {
            return;
        }
        createDefaultAdmin();
        utilityService.deleteByUtilKey("admin_not_exists");
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setLogin(LOGIN);
        admin.setPassword(PASSWORD);
        admin.setMoney(100000L);
        admin = userService.registerNewUser(admin, Role.ADMIN);
        createDefaultShip(admin);
    }

    private void createDefaultShip(User admin) {
        StarShip adminStarShip = new StarShip();
        adminStarShip.setTitle("Admin Starship");
        adminStarShip.setX(10f);
        adminStarShip.setY(10f);
        adminStarShip.setSpeedX(0f);
        adminStarShip.setSpeedY(0f);
        adminStarShip.setPositionIteration(0L);
        adminStarShip.setUserId(admin.getId());
        adminStarShip.setObjectTypeDescription(objectTypeDescriptionRepository.findById(66L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find ObjectTypeDescription with such id")));
        adminStarShip.setStatus(ObjectStatus.IN_SPACE);
        adminStarShip = starShipRepository.save(adminStarShip);
        BasicObject radar = createDefaultRadar();
        radar.setAttachedToSocket(1109L);
        radar.setAttachedToShip(adminStarShip);
        BasicObject engine = createDefaultEngine();
        engine.setAttachedToShip(adminStarShip);
        engine.setAttachedToSocket(1089L);
        admin.setLocation(adminStarShip);
    }

    private BasicObject createDefaultEngine() {
        BasicObject engine = new BasicObject();
        engine.setObjectTypeDescription(objectTypeDescriptionRepository.findById(33L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find ObjectTypeDescription with such id")));
        return basicObjectRepository.save(engine);
    }

    private BasicObject createDefaultRadar() {
        BasicObject radar = new BasicObject();
        radar.setObjectTypeDescription(objectTypeDescriptionRepository.findById(44L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find ObjectTypeDescription with such id")));
        return basicObjectRepository.save(radar);
    }
}
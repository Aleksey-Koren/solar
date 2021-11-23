package io.solar.service;

import io.solar.entity.User;
import io.solar.entity.Utility;
import io.solar.security.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class InitService {

    private UtilityService utilityService;
    private UserService userService;

    private final String LOGIN = "admin";
    private final String PASSWORD = "admin";
    @Value("${app.admin_not_exists}")
    private String adminNotExists;

    @Autowired
    public InitService(UtilityService utilityService, UserService userService) {
        this.utilityService = utilityService;
        this.userService = userService;
    }

    @PostConstruct
    private void startup() {
        defaultAdminInitialization();
    }

    private void defaultAdminInitialization() {
        if (adminNotExists != null && (adminNotExists.equals("yes") || adminNotExists.equals("no"))) {
            if (adminNotExists.equals("yes")) {
                User adminFromDb = userService.findByLogin(LOGIN);
                if (adminFromDb == null) {
                    createDefaultAdmin();
                }
                setUtility("no");
            }
        }else{
            String value = utilityService.getValue("admin_not_exists").orElse("");
            if (value.equals("yes")) {
                User adminFromDb = userService.findByLogin(LOGIN);
                if (adminFromDb == null) {
                    createDefaultAdmin();
                }
                setUtility("no");
            }
        }
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setLogin(LOGIN);
        admin.setPassword(PASSWORD);
        User adminFromDb = userService.findByLogin(LOGIN);
        if (adminFromDb != null) {
            throw new ServiceException("Trying to create default admin, whet it exists");
        }
        userService.registerNewUser(admin, Role.ADMIN);
    }

    private void setUtility(String value) {
        Utility utility = new Utility("admin_not_exists", value);
        utilityService.save(utility);
    }


}

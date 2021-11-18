package io.solar.facade;

import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static io.solar.controller.AuthController.hasPermissions;

@Component
@Transactional
public class UserFacade {

    private final UserService userService;

    @Autowired
    public UserFacade(UserService userService) {
        this.userService = userService;
    }


    public User update(User dto, Principal principal) {



        User user = userService.findByLogin(principal.getName());
        boolean canEdit = user.getId().equals(dto.getId()) || hasPermissions(List.of("EDIT_USER"));
        if (canEdit) {
            return updateUserTitle(dto.getId(), dto.getTitle());
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to edit user's title");
        }
    }
}

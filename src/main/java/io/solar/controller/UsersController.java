package io.solar.controller;

import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static io.solar.controller.AuthController.hasPermissions;


@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<User> getList(
            Pageable pageable,
            @RequestParam("login") String login,
            @RequestParam("title") String title
    ) {
        boolean canEdit = hasPermissions(List.of("EDIT_USER"));
        return userService.getAllUsers(pageable, login, title, canEdit);
    }

    @GetMapping("{id}")
    public User getOne(@PathVariable("id") long id) {
        boolean canEdit = hasPermissions(List.of("EDIT_USER"));
        return userService.getUserById(id, canEdit);
    }

    // TODO: edit request path on front-end
    //  (make POST to /api/users/{id} instead of /api/users with 'id' and 'title' defined in payload)
    @PostMapping("{id}")
    public User updateUserTitle(
            @PathVariable("id") long id,
            @RequestParam("title") String title,
            Principal principal
    ) {
        User user = userService.findByLogin(principal.getName());
        boolean canEdit = user.getId() == id || hasPermissions(List.of("EDIT_USER"));

        if (canEdit) {
            return userService.updateUserTitle(id, title);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permission to edit user's title");
        }
    }
}

package io.solar.controller;

import io.solar.dto.UserFilter;
import io.solar.entity.User;
import io.solar.facade.UserFacade;
import io.solar.service.PermissionService;
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

    private final UserFacade userFacade;
    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public UsersController(UserFacade userFacade,
                           UserService userService,
                           PermissionService permissionService) {
        this.userFacade = userFacade;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @GetMapping
    public Page<User> getList(
            Pageable pageable,
            @RequestParam(value = "login", required = false) String login,
            @RequestParam(value = "title", required = false) String title
    ) {
        boolean canEdit = hasPermissions(List.of("EDIT_USER"));
        return userService.getAllUsers(pageable, new UserFilter(login, title), canEdit);
    }

    @GetMapping("{id}")
    public User getOne(@PathVariable("id") long id) {
        boolean canEdit = hasPermissions(List.of("EDIT_USER"));
        return userService.getUserById(id, canEdit);
    }

    @PutMapping
    public User updateUser(@RequestBody User dto, Principal principal) {
        return userFacade.update(dto, principal);
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

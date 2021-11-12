package io.solar.controller;

import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<User> getList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int pageSize,
            @RequestParam("login") String login,
            @RequestParam("title") String title
    ) {
        PageRequest paging = PageRequest.of(page, pageSize);
        boolean canEdit = hasPermissions(List.of("EDIT_USER"));
        return userService.getAllUsers(paging, login, title, canEdit);
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

    private boolean hasPermissions(List<String> permissions) {
        List<String> authorities = new ArrayList<>();
        SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities()
                .forEach(a -> authorities.add(a.getAuthority()));

        return authorities.containsAll(permissions);
    }
}

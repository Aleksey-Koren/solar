package io.solar.controller;

import io.solar.entity.User;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam("title") String title,
            SecurityContextHolderAwareRequestWrapper rw
    ) {
        PageRequest paging = PageRequest.of(page, pageSize);
        return userService.getAllUsers(paging, login, title, rw.isUserInRole("edit-user"));
    }

    @GetMapping("{id}")
    public User getOne(@PathVariable("id") long id, SecurityContextHolderAwareRequestWrapper rw) {
        return userService.getUserById(id, rw.isUserInRole("edit-user"));
    }

    // TODO: edit request path on front-end
    //  (make POST to /api/users/{id} instead of /api/users with 'id' and 'title' defined in payload)
    @PostMapping("{id}")
    public User updateUser(
            @PathVariable("id") long id,
            @RequestParam("title") String title,
            SecurityContextHolderAwareRequestWrapper rw
    ) {
        return userService.updateUserTitle(id, title, rw.isUserInRole("edit-user"));
    }
}

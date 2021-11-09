package io.solar.controller;

import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
            Pageable pageable,
            Transaction transaction,
            @AuthData User user,
            @RequestParam("login") String login,
            @RequestParam("title") String title
    ) {
        return userService.getAllUsers(user, transaction, login, title, pageable);
    }

    @GetMapping("{id}")
    public User getOne(@PathVariable("id") Long id, Transaction transaction, @AuthData User user) {
        return userService.getUserById(id, transaction, user);
    }

    @PostMapping
    public User updateUser(@AuthData User user, @RequestBody User payload, Transaction transaction) {
        boolean canEdit = AuthController.userCan(user, "edit-user", transaction);
        if (!((user.getId() != null && user.getId().equals(payload.getId())) || canEdit)) {
            throw new RuntimeException("no permissions");
        }
        if (payload.getId() == null) {
            throw new RuntimeException("bad request, id should be defined");
        }

        return userService.updateUserTitle(payload.getId(), payload.getTitle(), canEdit);
    }
}

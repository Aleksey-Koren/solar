package io.solar.controller;

import io.solar.dto.UserDto;
import io.solar.dto.UserFilter;
import io.solar.entity.User;
import io.solar.facade.UserFacade;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static io.solar.controller.AuthController.hasPermissions;


@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserFacade userFacade;
    private final UserService userService;

    @Autowired
    public UsersController(UserFacade userFacade,
                           UserService userService) {
        this.userFacade = userFacade;
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDto> getList(
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
    public UserDto updateUser(@RequestBody UserDto dto, Principal principal) {
        return userFacade.update(dto, principal);
    }
}

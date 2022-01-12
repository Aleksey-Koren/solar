package io.solar.controller;

import io.solar.dto.UserDto;
import io.solar.specification.filter.UserFilter;
import io.solar.entity.User;
import io.solar.facade.UserFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static io.solar.controller.AuthController.hasPermissions;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;
    private final UserFacade userFacade;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME','EDIT_USER')")
    @Transactional
    public Page<UserDto> getList(Pageable pageable, UserFilter userFilter) {

        return userService.getAllUsers(pageable, userFilter);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_USER')")
    @Transactional
    public ResponseEntity<UserDto> getOne(@PathVariable("id") long userId) {
        UserDto out = userService.getUserById(userId);
        return ResponseEntity.ok(out);
    }

    @PutMapping("{id}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_USER')")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") long id,
                                              @RequestBody UserDto dto,
                                              Principal principal) {
        User authUser = userService.findByLogin(principal.getName());
        User userToChange = userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't found user with such id"));
        userToChange.setId(id);
        if (authUser.getId() == id && !hasPermissions(List.of("EDIT_USER"))) {
            UserDto responseDto = userFacade.updateOnlyTitle(dto);
            return ResponseEntity.ok().body(responseDto);
        } else if (authUser.getId() == id && hasPermissions(List.of("EDIT_USER"))) {
            UserDto responseDto = userFacade.updateGameParameters(dto);
            return ResponseEntity.ok().body(responseDto);
        } else if (hasPermissions(List.of("EDIT_USER")) && !userFacade.userHasPermission(userToChange, "EDIT_USER")) {
            UserDto responseDto = userFacade.updateGameParameters(dto);
            return ResponseEntity.ok().body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new UserDto());
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (!hasPermissions(List.of("EDIT_USER", "DELETE_USER"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userService.findById(id).ifPresent(userService::delete);
        return ResponseEntity.noContent().build();
    }
}
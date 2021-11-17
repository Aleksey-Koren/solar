package io.solar.controller;

import io.solar.dto.PermissionDto;
import io.solar.entity.User;
import io.solar.service.PermissionService;
import io.solar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

import static io.solar.controller.AuthController.hasPermissions;


@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

    private final PermissionService permissionService;
    private final UserService userService;

    @Autowired
    public PermissionsController(PermissionService permissionService,
                                 UserService userService) {
        this.permissionService = permissionService;
        this.userService = userService;
    }


    @GetMapping
    public List<PermissionDto> getPermissions(
            @RequestParam(required = false, name = "userId") Long userId,
            Principal principal
    ) {
        if (userId == null) {
            if (!hasPermissions(List.of("SEE_PERMISSIONS"))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Viewing permissions is not allowed for you");
            }
            return permissionService.getAll();
        } else {
            User user = userService.findByLogin(principal.getName());
            if (!(user.getId().equals(userId) || hasPermissions(List.of("SEE_PERMISSIONS")))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Viewing permissions is not allowed for you");
            }
            return permissionService.getPermissionsByUserId(userId);
        }
    }

    @PostMapping
    public PermissionDto savePermission(@RequestBody PermissionDto dto) {
        if (!hasPermissions(List.of("EDIT_PERMISSION"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Editing permissions is not allowed for you");
        }
        return permissionService.save(dto);
    }

    // TODO: edit request endpoint on front-end
    //  (was /api/permissions/elevate)
    //  probably move to UsersController and change return type?
    @PostMapping("/users/{id}/assign")
    public PermissionDto assignPermissionToUser(@PathVariable("id") Long userId, @RequestBody PermissionDto dto) {
        if (!hasPermissions(List.of("ASSIGN_PERMISSION"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assigning permissions is not allowed for you");
        }
        return permissionService.assignPermission(userId, dto);
    }

    // TODO: edit request endpoint on front-end
    //  (was /api/permissions/elevate)
    //  probably move to UsersController and change return type?
    @PreAuthorize("hasAuthority('REVOKE_PERMISSION')")
    @PostMapping("/users/{id}/revoke")
    public PermissionDto revokePermissionFromUser(@PathVariable("id") Long userId, @RequestBody PermissionDto dto) {
//        if (!hasPermissions(List.of("REVOKE_PERMISSION"))) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Revoking permissions is not allowed for you");
//        }
        return permissionService.revokePermission(userId, dto);
    }
}

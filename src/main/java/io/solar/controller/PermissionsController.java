package io.solar.controller;

import io.solar.dto.PermissionDto;
import io.solar.facade.PermissionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionsController {

    private final PermissionFacade permissionFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('SEE_PERMISSIONS')")
    public List<PermissionDto> getPermissions(@RequestParam(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return permissionFacade.getAll();
        } else {
            return permissionFacade.getPermissionsByUserId(userId);
        }
    }

    // TODO: edit request endpoint on front-end
    //  (was /api/permissions/elevate)
    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSIONS')")
    public Set<PermissionDto> updateUserPermissions(@PathVariable("id") Long userId, @RequestBody Set<PermissionDto> dtos) {
        return permissionFacade.assignPermissionsToUser(userId, dtos);
    }
}

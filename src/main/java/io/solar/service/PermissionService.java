package io.solar.service;

import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserService userService;

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    public List<Permission> getPermissionsByUserId(Long id) {
        return permissionRepository.findByUsersId(id);
    }

    public Set<Permission> assignPermissions(Long userId, Set<Permission> permissions) {
        User userToEdit = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id"));
        userToEdit.setPermissions(permissions);

        return userService.update(userToEdit).getPermissions();
    }
}

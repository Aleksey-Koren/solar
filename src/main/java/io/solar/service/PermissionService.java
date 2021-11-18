package io.solar.service;

import io.solar.dto.PermissionDto;
import io.solar.entity.Permission;
import io.solar.entity.User;
import io.solar.mapper.PermissionMapper;
import io.solar.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
public class PermissionService {

    private final PermissionMapper mapper;
    private final PermissionRepository permissionRepository;
    private final UserService userService;

    @Autowired
    public PermissionService(PermissionMapper mapper,
                             PermissionRepository permissionRepository,
                             UserService userService) {
        this.mapper = mapper;
        this.permissionRepository = permissionRepository;
        this.userService = userService;
    }


    public List<PermissionDto> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<PermissionDto> getPermissionsByUserId(Long id) {
        List<Permission> permissions = permissionRepository.findByUsersId(id);
        return permissions.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public PermissionDto assignPermission(Long userId, PermissionDto dto) {
        if (dto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission is blank");
        }
        User userToEdit = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id"));

        Permission permission = mapper.toEntity(dto);
        if (userToEdit.getPermissions().contains(permission)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already have such permission");
        }
        userToEdit.getPermissions().add(permission);
        userService.update(userToEdit);

        return mapper.toDto(permission);
    }

    public PermissionDto revokePermission(Long userId, PermissionDto dto) {
        if (dto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission is blank");
        }
        User userToEdit = userService.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with such id"));

        Permission permission = mapper.toEntity(dto);
        if (!userToEdit.getPermissions().contains(permission)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User doesn't have such permission");
        }
        userToEdit.getPermissions().remove(permission);
        userService.update(userToEdit);

        return mapper.toDto(permission);
    }
}

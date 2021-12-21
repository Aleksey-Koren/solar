package io.solar.facade;

import io.solar.dto.PermissionDto;
import io.solar.entity.Permission;
import io.solar.mapper.PermissionMapper;
import io.solar.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Transactional
@RequiredArgsConstructor
public class PermissionFacade {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public List<PermissionDto> getAll() {
        List<Permission> permissions = permissionService.getAll();
        return permissions.stream().map(permissionMapper::toDto).collect(Collectors.toList());
    }

    public List<PermissionDto> getPermissionsByUserId(Long userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return permissions.stream().map(permissionMapper::toDto).collect(Collectors.toList());
    }

    public Set<PermissionDto> assignPermissionsToUser(Long userId, Set<PermissionDto> dtos) {
        Set<Permission> permissions = dtos.stream().map(permissionMapper::toEntity).collect(Collectors.toSet());
        Set<Permission> out = permissionService.assignPermissions(userId, permissions);
        return out.stream().map(permissionMapper::toDto).collect(Collectors.toSet());
    }
}

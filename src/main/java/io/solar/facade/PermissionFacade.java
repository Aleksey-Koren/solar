package io.solar.facade;

import io.solar.dto.PermissionDto;
import io.solar.entity.Permission;
import io.solar.mapper.PermissionMapper;
import io.solar.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Transactional
public class PermissionFacade {

    private final PermissionService permissionService;
    private final PermissionMapper mapper;

    @Autowired
    public PermissionFacade(PermissionService permissionService, PermissionMapper mapper) {
        this.permissionService = permissionService;
        this.mapper = mapper;
    }


    public List<PermissionDto> getAll() {
        List<Permission> permissions = permissionService.getAll();
        return permissions.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public List<PermissionDto> getPermissionsByUserId(Long userId) {
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        return permissions.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Set<PermissionDto> assignPermissionsToUser(Long userId, Set<PermissionDto> dtos) {
        Set<Permission> permissions = dtos.stream().map(mapper::toEntity).collect(Collectors.toSet());
        Set<Permission> out = permissionService.assignPermissions(userId, permissions);
        return out.stream().map(mapper::toDto).collect(Collectors.toSet());
    }
}

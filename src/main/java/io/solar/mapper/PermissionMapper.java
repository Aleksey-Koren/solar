package io.solar.mapper;

import io.solar.dto.PermissionDto;
import io.solar.entity.Permission;
import io.solar.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


@Component
public class PermissionMapper implements EntityDtoMapper<Permission, PermissionDto> {

    private final PermissionRepository repository;

    @Autowired
    public PermissionMapper(PermissionRepository repository) {
        this.repository = repository;
    }


    @Override
    public Permission toEntity(PermissionDto dto) {
        Permission entity;
        if (dto.getId() == null) {
            entity = new Permission();
        } else {
            entity = repository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No permission with such id"));
        }
        entity.setTitle(dto.getTitle());
        return entity;
    }

    @Override
    public PermissionDto toDto(Permission entity) {
        PermissionDto dto = new PermissionDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        return dto;
    }
}

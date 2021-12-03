package io.solar.mapper;

import io.solar.dto.InventoryModificationDto;
import io.solar.entity.inventory.ObjectModificationType;
import io.solar.repository.ObjectModificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class InventoryModificationMapper implements EntityDtoMapper<ObjectModificationType, InventoryModificationDto> {

    private final ObjectModificationTypeRepository objectModificationTypeRepository;

    @Autowired
    public InventoryModificationMapper(ObjectModificationTypeRepository objectModificationTypeRepository) {
        this.objectModificationTypeRepository = objectModificationTypeRepository;
    }

    @Override
    public ObjectModificationType toEntity(InventoryModificationDto dto) {

        return Objects.isNull(dto.getId())
                ? createModification(dto)
                : findModification(dto);
    }

    @Override
    public InventoryModificationDto toDto(ObjectModificationType entity) {

        return new InventoryModificationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getData(),
                entity.getDescription()
        );
    }

    private ObjectModificationType findModification(InventoryModificationDto dto) {
        ObjectModificationType objectModificationType = objectModificationTypeRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object modification with id = %d", dto.getId())
                ));

        objectModificationType.setTitle(dto.getTitle());
        objectModificationType.setData(dto.getData());
        objectModificationType.setDescription(dto.getDescription());

        return objectModificationType;
    }

    private ObjectModificationType createModification(InventoryModificationDto dto) {

        return ObjectModificationType.builder()
                .id(null)
                .title(dto.getTitle())
                .data(dto.getData())
                .description(dto.getDescription())
                .build();
    }
}
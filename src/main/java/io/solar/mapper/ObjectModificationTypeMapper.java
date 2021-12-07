package io.solar.mapper;

import io.solar.dto.ObjectModificationTypeDto;
import io.solar.entity.objects.ObjectModificationType;
import io.solar.repository.ObjectModificationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class ObjectModificationTypeMapper implements EntityDtoMapper<ObjectModificationType, ObjectModificationTypeDto> {

    private final ObjectModificationTypeRepository objectModificationTypeRepository;

    @Autowired
    public ObjectModificationTypeMapper(ObjectModificationTypeRepository objectModificationTypeRepository) {
        this.objectModificationTypeRepository = objectModificationTypeRepository;
    }

    @Override
    public ObjectModificationType toEntity(ObjectModificationTypeDto dto) {

        return Objects.isNull(dto.getId())
                ? createModification(dto)
                : findModification(dto);
    }

    @Override
    public ObjectModificationTypeDto toDto(ObjectModificationType entity) {

        return new ObjectModificationTypeDto(
                entity.getId(),
                entity.getTitle(),
                entity.getData(),
                entity.getDescription()
        );
    }

    private ObjectModificationType findModification(ObjectModificationTypeDto dto) {
        ObjectModificationType objectModificationType = objectModificationTypeRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("Cannot find object modification with id = %d", dto.getId())
                ));

        objectModificationType.setTitle(dto.getTitle());
        objectModificationType.setData(dto.getData());
        objectModificationType.setDescription(dto.getDescription());

        return objectModificationType;
    }

    private ObjectModificationType createModification(ObjectModificationTypeDto dto) {

        return ObjectModificationType.builder()
                .id(null)
                .title(dto.getTitle())
                .data(dto.getData())
                .description(dto.getDescription())
                .build();
    }
}
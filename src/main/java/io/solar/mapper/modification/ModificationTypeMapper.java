package io.solar.mapper.modification;

import io.solar.dto.modification.ModificationDto;
import io.solar.dto.modification.ModificationTypeDto;
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationType;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.modification.ModificationTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationTypeMapper implements EntityDtoMapper<ModificationType, ModificationTypeDto> {

    private final ModificationTypeService modificationTypeService;
    private final ModificationMapper modificationMapper;

    @Override
    public ModificationType toEntity(ModificationTypeDto dto) {
        return dto.getId() == null
                ? createModificationType(dto)
                : updateModificationType(dto);
    }

    @Override
    public ModificationTypeDto toDto(ModificationType entity) {
        List<ModificationDto> modificationDtoList = entity.getModifications()
                .stream()
                .map(modificationMapper::toDto)
                .toList();

        return ModificationTypeDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .modificationDtoList(modificationDtoList)
                .build();
    }

    private ModificationType createModificationType(ModificationTypeDto dto) {

        return ModificationType.builder()
                .title(dto.getTitle())
                .modifications(retrieveModificationList(dto))
                .build();
    }

    private ModificationType updateModificationType(ModificationTypeDto dto) {
        ModificationType modificationType = modificationTypeService.getById(dto.getId());

        modificationType.setTitle(dto.getTitle());
        modificationType.setModifications(retrieveModificationList(dto));

        return modificationType;
    }

    private List<Modification> retrieveModificationList(ModificationTypeDto dto) {
        if (dto.getModificationDtoList() != null) {

            return dto.getModificationDtoList()
                    .stream()
                    .map(modificationMapper::toEntity)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}

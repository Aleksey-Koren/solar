package io.solar.mapper.modification;

import io.solar.dto.modification.ModificationDto;
import io.solar.dto.modification.ParameterModificationDto;
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ParameterModification;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.modification.ModificationService;
import io.solar.service.modification.ModificationTypeService;
import io.solar.service.object.ObjectTypeDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationMapper implements EntityDtoMapper<Modification, ModificationDto> {

    private final ModificationService modificationService;
    private final ModificationTypeService modificationTypeService;
    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final ParameterModificationMapper parameterModificationMapper;

    @Override
    public Modification toEntity(ModificationDto dto) {

        return dto.getId() == null
                ? createModification(dto)
                : updateModification(dto);
    }

    @Override
    public ModificationDto toDto(Modification entity) {
        List<ParameterModificationDto> parameterModificationDtoList = entity.getParameterModifications()
                .stream()
                .map(parameterModificationMapper::toDto)
                .toList();

        List<Long> objectTypeDescriptionsIds = entity.getAvailableObjectTypeDescriptions()
                .stream()
                .map(ObjectTypeDescription::getId)
                .toList();

        return ModificationDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .level(entity.getLevel())
                .modificationTypeId(entity.getModificationType().getId())
                .parameterModificationDtoList(parameterModificationDtoList)
                .availableObjectTypeDescriptionsIds(objectTypeDescriptionsIds)
                .build();
    }

    private Modification createModification(ModificationDto dto) {
        List<ParameterModification> parameterModifications = null;
        List<ObjectTypeDescription> objectTypeDescriptions = null;

        if (dto.getParameterModificationDtoList() != null) {
            parameterModifications = dto.getParameterModificationDtoList()
                    .stream()
                    .map(parameterModificationMapper::toEntity)
                    .toList();
        }

        if (dto.getAvailableObjectTypeDescriptionsIds() != null) {
            objectTypeDescriptions = dto.getAvailableObjectTypeDescriptionsIds()
                    .stream()
                    .map(objectTypeDescriptionService::getById)
                    .toList();
        }

        return Modification.builder()
                .description(dto.getDescription())
                .level(dto.getLevel())
                .modificationType(modificationTypeService.getById(dto.getModificationTypeId()))
                .parameterModifications(parameterModifications)
                .availableObjectTypeDescriptions(objectTypeDescriptions)
                .build();
    }

    private Modification updateModification(ModificationDto dto) {
        Modification modification = modificationService.getById(dto.getId());
        List<ParameterModification> parameterModifications = null;

        if (dto.getParameterModificationDtoList() != null) {
            parameterModifications = dto.getParameterModificationDtoList()
                    .stream()
                    .map(parameterModificationMapper::toEntity)
                    .toList();
        }

        modification.setDescription(dto.getDescription() != null ? dto.getDescription() : modification.getDescription());
        modification.setParameterModifications(parameterModifications == null ? modification.getParameterModifications() : parameterModifications);

        return modification;
    }

}

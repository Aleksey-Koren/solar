package io.solar.mapper.modification;

import io.solar.dto.modification.ModificationDto;
import io.solar.dto.modification.ParameterModificationDto;
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ParameterModification;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.modification.ModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationMapper implements EntityDtoMapper<Modification, ModificationDto> {

    private final ModificationService modificationService;
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

        return ModificationDto.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .parameterModificationDtoList(parameterModificationDtoList)
                .build();
    }

    private Modification createModification(ModificationDto dto) {
        List<ParameterModification> parameterModifications = null;

        if (dto.getParameterModificationDtoList() != null) {
            parameterModifications = dto.getParameterModificationDtoList()
                    .stream()
                    .map(parameterModificationMapper::toEntity)
                    .toList();
        }

        return Modification.builder()
                .description(dto.getDescription())
                .parameterModifications(parameterModifications)
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

        modification.setDescription(dto.getDescription());
        modification.setParameterModifications(parameterModifications == null ? modification.getParameterModifications() : parameterModifications);

        return modification;
    }

}

package io.solar.mapper.modification;

import io.solar.dto.modification.ParameterModificationDto;
import io.solar.entity.modification.ParameterModification;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.modification.ModificationService;
import io.solar.service.modification.ParameterModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParameterModificationMapper implements EntityDtoMapper<ParameterModification, ParameterModificationDto> {

    private final ParameterModificationService parameterModificationService;
    private final ModificationService modificationService;

    @Override
    public ParameterModification toEntity(ParameterModificationDto dto) {

        return dto.getId() == null
                ? createParameterModification(dto)
                : updateParameterModification(dto);
    }

    @Override
    public ParameterModificationDto toDto(ParameterModification entity) {

        return ParameterModificationDto.builder()
                .id(entity.getId())
                .parameterType(entity.getParameterType())
                .modificationId(entity.getModification().getId())
                .modificationValue(entity.getModificationValue())
                .build();
    }

    private ParameterModification createParameterModification(ParameterModificationDto dto) {

        return ParameterModification.builder()
                .parameterType(dto.getParameterType())
                .modification(modificationService.getById(dto.getModificationId()))
                .modificationValue(dto.getModificationValue())
                .build();
    }

    private ParameterModification updateParameterModification(ParameterModificationDto dto) {
        ParameterModification parameterModification = parameterModificationService.getById(dto.getId());

        parameterModification.setParameterType(dto.getParameterType());
        parameterModification.setModification(modificationService.getById(dto.getModificationId()));
        parameterModification.setModificationValue(dto.getModificationValue());

        return parameterModification;
    }
}

package io.solar.facade.modifications;

import io.solar.dto.modification.ModificationDto;
import io.solar.mapper.modification.ModificationMapper;
import io.solar.service.modification.ModificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificationFacade {

    private final ModificationMapper modificationMapper;
    private final ModificationService modificationService;

    public ModificationDto save(ModificationDto dto) {
        return modificationMapper.toDto(modificationService.save(modificationMapper.toEntity(dto)));
    }
}

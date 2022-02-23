package io.solar.facade.modifications;

import io.solar.dto.modification.ModificationTypeDto;
import io.solar.mapper.modification.ModificationTypeMapper;
import io.solar.service.modification.ModificationTypeService;
import io.solar.specification.filter.ModificationTypeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificationTypeFacade {

    private final ModificationTypeService modificationTypeService;
    private final ModificationTypeMapper modificationTypeMapper;

    public ModificationTypeDto save(ModificationTypeDto dto) {
        return modificationTypeMapper.toDto(modificationTypeService.save(modificationTypeMapper.toEntity(dto)));
    }

    public ModificationTypeDto getById(Long id) {
        return modificationTypeMapper.toDto(modificationTypeService.getById(id));
    }

    public Page<ModificationTypeDto> getAll(Pageable pageable, ModificationTypeFilter filter) {
        return modificationTypeService.findAll(pageable, filter).map(modificationTypeMapper::toDto);
    }

    public void deleteById(Long id) {
        modificationTypeService.deleteById(id);
    }
}
package io.solar.facade.modifications;

import groovyjarjarantlr4.v4.runtime.atn.PredicateEvalInfo;
import io.solar.dto.modification.ApplyModificationDto;
import io.solar.dto.modification.ModificationDto;
import io.solar.entity.User;
import io.solar.entity.modification.Modification;
import io.solar.mapper.modification.ModificationMapper;
import io.solar.service.UserService;
import io.solar.service.modification.ModificationService;
import io.solar.specification.ModificationSpecification;
import io.solar.specification.filter.ModificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationFacade {

    private final ModificationMapper modificationMapper;
    private final ModificationService modificationService;
    private final UserService userService;

    public List<ModificationDto> findAll(ModificationFilter modificationFilter, Pageable pageable) {

        return modificationService.findAll(new ModificationSpecification(modificationFilter), pageable)
                .map(modificationMapper::toDto)
                .toList();
    }

    public ModificationDto save(ModificationDto dto) {
        return modificationMapper.toDto(modificationService.save(modificationMapper.toEntity(dto)));
    }

    public void delete(Long modificationId) {
        Modification modification = modificationService.getById(modificationId);

        modificationService.delete(modification);
    }

    public void applyModification(ApplyModificationDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
    }
}

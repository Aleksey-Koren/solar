package io.solar.controller.modification;

import io.solar.dto.modification.ModificationTypeDto;
import io.solar.facade.modifications.ModificationTypeFacade;
import io.solar.specification.filter.ModificationTypeFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/modification/type")
@RequiredArgsConstructor
public class ModificationTypeController {

    private final ModificationTypeFacade modificationTypeFacade;

    @PostMapping("api/modifications/type")
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_MODOFICATION')")
    public ResponseEntity<ModificationTypeDto> save(@RequestBody ModificationTypeDto dto) {
        return ResponseEntity.ok(modificationTypeFacade.save(dto));
    }

    @GetMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAnyAuthority('EDIT_MODOFICATION', 'PLAY_THE_GAME')")
    public ModificationTypeDto findById(@PathVariable Long id) {
        return modificationTypeFacade.getById(id);
    }

    @GetMapping
    @Transactional
    @PreAuthorize("hasAnyAuthority('EDIT_MODOFICATION', 'PLAY_THE_GAME')")
    public Page<ModificationTypeDto> findAll(@PageableDefault(size = 20) Pageable pageable, ModificationTypeFilter filter) {
        return modificationTypeFacade.getAll(pageable, filter);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_MODOFICATION')")
    public void deleteById(@PathVariable Long id) {
        modificationTypeFacade.deleteById(id);
    }
}
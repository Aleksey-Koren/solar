package io.solar.controller.modification;

import io.solar.dto.modification.ApplyModificationDto;
import io.solar.dto.modification.ModificationDto;
import io.solar.facade.modifications.ModificationFacade;
import io.solar.specification.filter.ModificationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/modification")
@RequiredArgsConstructor
public class ModificationController {

    private final ModificationFacade modificationFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<ModificationDto> findAll(ModificationFilter modificationFilter, @PageableDefault Pageable pageable) {

        return modificationFacade.findAll(modificationFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_MODIFICATIONS')")
    @Transactional
    public ResponseEntity<ModificationDto> save(@RequestBody ModificationDto dto) {

        return ResponseEntity.ok(modificationFacade.save(dto));
    }

    @DeleteMapping("/{modificationId}")
    @PreAuthorize("hasAuthority('EDIT_MODIFICATIONS')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long modificationId) {
        modificationFacade.delete(modificationId);
    }

    @PatchMapping()
    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public ResponseEntity<ApplyModificationDto> applyModificationStarShip(@RequestBody ApplyModificationDto dto, Principal principal) {
        dto = modificationFacade.applyModificationStarShip(dto, principal);
        return ResponseEntity.ok(dto);
    }
}
package io.solar.controller.modification;

import io.solar.dto.modification.ModificationDto;
import io.solar.facade.modifications.ModificationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modifications")
@RequiredArgsConstructor
public class ModificationController {

    private final ModificationFacade modificationFacade;

    @PostMapping
    @Transactional
    @PreAuthorize("hasAuthority('EDIT_MODIFICATIONS')")
    public ResponseEntity<ModificationDto> save(@RequestBody ModificationDto dto) {
        return ResponseEntity.ok(modificationFacade.save(dto));
    }
}
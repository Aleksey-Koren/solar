package io.solar.controller.modification;

import io.solar.dto.modification.ModificationPriceDto;
import io.solar.entity.User;
import io.solar.facade.modifications.ModificationPriceFacade;
import io.solar.service.UserService;
import io.solar.specification.filter.ModificationPriceFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/modification/price")
@RequiredArgsConstructor
public class ModificationPriceController {

    private final ModificationPriceFacade modificationPriceFacade;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<ModificationPriceDto> findAll(ModificationPriceFilter modificationPriceFilter, @PageableDefault Pageable pageable) {

        return modificationPriceFacade.findAll(modificationPriceFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> save(@RequestBody ModificationPriceDto modificationPriceDto, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.status(modificationPriceFacade.createOrUpdateModificationPrice(modificationPriceDto, user)).build();
    }

}
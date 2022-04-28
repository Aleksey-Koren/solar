package io.solar.controller;

import io.solar.dto.PlanetDto;
import io.solar.facade.PlanetFacade;
import io.solar.mapper.PlanetMapper;
import io.solar.service.PlanetService;
import io.solar.specification.filter.PlanetFilter;
import io.solar.dto.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/planet")
public class PlanetController {

    private final PlanetService planetService;
    private final PlanetFacade planetFacade;

    @Transactional
    @PreAuthorize("hasAuthority('EDIT_PLANET')")
    @PostMapping
    public PlanetDto save(@RequestBody PlanetDto planetDto) {

        return planetFacade.save(planetDto);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PLANET')")
    @GetMapping("/{id}")
    public PlanetDto findById(@PathVariable("id") Long id) {

        return planetFacade.findById(id);
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PLANET')")
    @GetMapping
    public Page<PlanetDto> findAll(@PageableDefault Pageable pageable, PlanetFilter planetFilter) {

        return planetFacade.findAll(planetFilter, pageable);
    }

    @Transactional
    @RequestMapping("/utils/dropdown")
    public List<Option> dropdown() {
        return planetService.findAll().stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }
}
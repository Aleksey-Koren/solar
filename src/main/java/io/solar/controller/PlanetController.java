package io.solar.controller;

import io.solar.dto.PlanetDto;
import io.solar.mapper.PlanetMapper;
import io.solar.service.PlanetService;
import io.solar.specification.filter.PlanetFilter;
import io.solar.utils.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/planet")
public class PlanetController {

    private PlanetService planetService;
    private PlanetMapper planetMapper;

    @Autowired
    public PlanetController(PlanetService planetService, PlanetMapper planetMapper) {
        this.planetService = planetService;
        this.planetMapper = planetMapper;
    }

    @PreAuthorize("hasAuthority('EDIT_PLANET')")
    @PostMapping
    public PlanetDto save(@RequestBody PlanetDto dto) {
        return planetMapper.toDto(planetService.save(planetMapper.toEntity(dto)));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PLANET')")
    @GetMapping("/{id}")
    public PlanetDto findById(@PathVariable("id") Long id) {
        return planetMapper.toDto(planetService.findById(id));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('PLAY_THE_GAME', 'EDIT_PLANET')")
    @GetMapping
    public Page<PlanetDto> findAll(@PageableDefault Pageable pageable, @RequestParam(value = "ids", required = false) List<Long> ids) {
        if(ids == null || ids.size() == 0) {
            return planetService.findAll(pageable).map(planetMapper::toDto);
        }else {
            return planetService.findAllFiltered(new PlanetFilter(ids), pageable).map(planetMapper::toDto);
        }
    }

    @Transactional
    @RequestMapping("/utils/dropdown")
    public List<Option> dropdown() {
        return planetService.findAll().stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }
}

package io.solar.controller;

import io.solar.dto.PlanetDto;
import io.solar.entity.Planet;
import io.solar.service.PlanetService;
import io.solar.utils.Option;
import io.solar.utils.db.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;


@Slf4j
@RestController
@RequestMapping("/api/planet")
public class PlanetController {

    private PlanetService planetService;

    @Autowired
    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @PreAuthorize("hasAuthority('EDIT_PLANETS')")
    @PostMapping
    public Planet save(@RequestBody Planet planet) {
        return planetService.save(planet);
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @GetMapping("/{id}")
    public PlanetDto findById(@PathVariable("id") Long id) {
        Planet planet = planetService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no planet with id = %s", id)));
        return new PlanetDto(planet);
    }

    @Transactional
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @GetMapping
    public Page<PlanetDto> findAll(@PageableDefault(size = 5, page = 0) Pageable pageable, @RequestParam(value = "ids", required = false) List<Long> ids) {
        if(ids == null || ids.size() == 0) {
            return planetService.findAll(pageable).map(PlanetDto::new);
        }else{
            List<PlanetDto> planets = planetService.findAllById(ids).stream()
                    .map(PlanetDto::new)
                    .collect(toList());

            int begin = (int)pageable.getOffset();
            int end = Math.min(begin + pageable.getPageSize(), planets.size());
            return new PageImpl<>(planets.subList(begin, end), pageable, planets.size());
        }
    }

    @Transactional
    @RequestMapping("/utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
        return planetService.findAll().stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }
}

package io.solar.controller;

import io.solar.dto.PlanetDTO;
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

    @PreAuthorize("hasAuthority(edit-planets)")
    @PostMapping
    public Planet save(@RequestBody Planet planet) {
        return planetService.save(planet);
    }

    @PreAuthorize("hasAuthority(play-the-game)")
    @GetMapping("/{id}")
    public PlanetDTO findById(@PathVariable("id") Long id) {
        Planet planet = planetService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("There is no planet with id = %s", id)));
        return new PlanetDTO(planet);
    }

    @PreAuthorize("hasAuthority(play-the-game)")
    @GetMapping
    public Page<PlanetDTO> findAll(@PageableDefault() Pageable pageable, @RequestParam("ids") List<Long> ids) {
        if(ids == null || ids.size() == 0) {
            return planetService.findAll(pageable).map(PlanetDTO::new);
        }else{
            List<PlanetDTO> planets = planetService.findAllById(ids).stream()
                    .map(PlanetDTO::new)
                    .collect(toList());
            return new PageImpl<PlanetDTO>(planets, pageable, planets.size());
        }
    }

//    @RequestMapping
//    public List<Planet> getAll(Transaction transaction) {
//        Query query = transaction.query("select * from planets");
//        List<Planet> existing = query.executeQuery(new PlanetMapper());
//        return existing;
//    }

//    public List<Planet> getByIds(List<Long> ids, Transaction transaction) {
//        Query query = transaction.query("select * from planets where id in (" +
//                ids.stream().map(v -> "?").collect(joining(", ")) + ")");
//        for (int i = 0; i < ids.size(); i++) {
//            query.setLong(i + 1, ids.get(i));
//        }
//        return query.executeQuery(new PlanetMapper());
//    }

    //TODO I should check business logic of this method to define authorities
    @RequestMapping("/utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
        return planetService.findAll().stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }
//    @RequestMapping("utils/dropdown")
//    public List<Option> dropdown(Transaction transaction) {
//        return getAll(transaction).stream().map(v -> new Option(v.getId(), v.getTitle())).collect(Collectors.toList());
//    }
}

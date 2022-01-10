package io.solar.controller;

import io.solar.dto.Option;
import io.solar.dto.StationDto;
import io.solar.facade.StationFacade;
import io.solar.service.StationService;
import io.solar.service.scheduler.GoodsGeneration;
import io.solar.specification.filter.StationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "api/station")
@Slf4j
@RequiredArgsConstructor
public class StationController {

    private final StationFacade stationFacade;
    private final StationService stationService;
    private final GoodsGeneration goodsGeneration;

    @PostMapping
    @PreAuthorize("hasAuthority('EDIT_STATION')")
    @Transactional
    public StationDto save(@RequestBody StationDto dto) {
        return stationFacade.save(dto);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_STATION', 'PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<StationDto> get(@PathVariable("id") Long id) {
        Optional<StationDto> station = stationFacade.findById(id);
        return station.isPresent() ? ResponseEntity.ok(station.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EDIT_STATION', 'PLAY_THE_GAME')")
    @Transactional
    public Page<StationDto> getAll(Pageable pageable, StationFilter stationFilter) {
        return stationFacade.findAll(pageable, stationFilter);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_STATION')")
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        stationService.deleteById(id);
    }

    @GetMapping("utils/dropdown")
    public List<Option> dropdown() {
        return stationService.findAll()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(toList());
    }

    @Scheduled(fixedDelayString = "#{@appProperties.getGoodsGenerationDelayMinutes()}",
            initialDelayString = "#{@appProperties.getGoodsInitialDelayMinutes()}",
            timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void generateGoods() {
        goodsGeneration.generateOnStations();
    }
}
package io.solar.controller;

import io.solar.config.AppProperties;
import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.Marketplace;
import io.solar.dto.StationDto;
import io.solar.entity.*;
import io.solar.entity.objects.StarShip;
import io.solar.facade.StationFacade;
import io.solar.service.StationService;
import io.solar.specification.filter.StationFilter;
import io.solar.service.data_generation.GoodsGeneration;
import io.solar.utils.Option;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "api/station")
@Slf4j
public class StationController {


    private final StationFacade stationFacade;
    private final StationService stationService;
    private final GoodsGeneration goodsGeneration;

    @Autowired
    public StationController(StationFacade stationFacade, StationService stationService, GoodsGeneration goodsGeneration, AppProperties appProperties) {
        this.stationFacade = stationFacade;
        this.stationService = stationService;
        this.goodsGeneration = goodsGeneration;
    }

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
    public Page<BasicObjectViewDto> getAll(Pageable pageable, StationFilter stationFilter) {

        return stationFacade.findAllAsBasicObjects(pageable, stationFilter);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('EDIT_STATION')")
    @Transactional
    public void delete(@PathVariable("id") Long id) {
        stationService.deleteById(id);
    }

    @GetMapping("utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
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









    //TODO Methods below aren't refactored

    @RequestMapping("user/marketplace")
    public Marketplace getMarketplace(@AuthData User user, Transaction transaction) {
        Long planetId = definePlanetId(user, transaction);
        Query query = transaction.query("select * from ");
        return new Marketplace(null, null, null);
    }

    private Long definePlanetId(User user, StarShip starShip) {
        Long planetId = user.getPlanet().getId();
        if(planetId != null) {
            return planetId;
        }
        if(starShip != null) {
//            return starShip.getPlanet();
            return null;
        } else {
            return 4L;//earth id
        }
    }
    private Long definePlanetId(User user, Transaction transaction) {
        Long planetId = user.getPlanet().getId();
        if(planetId != null) {
            return planetId;
        }
//        Optional<StarShip> starShip = starShipService.getActiveShip(user, transaction);
        Optional<StarShip> starShip = null;
        return definePlanetId(user, starShip.get());
    }
}
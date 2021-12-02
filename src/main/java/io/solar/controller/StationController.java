package io.solar.controller;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.Marketplace;
import io.solar.entity.*;
import io.solar.entity.objects.StarShip;
import io.solar.facade.StationFacade;
import io.solar.mapper.PopulationMapper;
import io.solar.service.StationService;
import io.solar.utils.Option;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "api/station")
@Slf4j
public class StationController {


    private final StationFacade stationFacade;
    private final StationService stationService;

    @Autowired
    public StationController(StationFacade stationFacade, StationService stationService) {
        this.stationFacade = stationFacade;
        this.stationService = stationService;
    }

    //    @PostMapping
//    @PreAuthorize("hasAuthority('EDIT_STATION')")
//    @Transactional
//    public Station save(@RequestBody Station station, @AuthData User user, Transaction transaction) {
//        if (!AuthController.userCan(user, "edit-station", transaction)) {
//            throw new RuntimeException("no privileges");
//        }
//        return stationRestUtils.save(station, transaction);
//    }

//    @GetMapping("{id}")
//    public ResponseEntity<Page<BasicObjectViewDto>> get(@PathVariable("id") Long id, Transaction transaction) {
//        return stationRestUtils.get(id, transaction);
//    }


    @GetMapping
    @PreAuthorize("hasAnyAuthority('EDIT_STATION', 'PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Page<BasicObjectViewDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(stationFacade.findAllAsBasicObjects(pageable));
    }

    @GetMapping("utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
        return stationService.findAll()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(toList());
    }


//    @DeleteMapping("{id}")
//    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
//        if (!AuthController.userCan(user, "edit-station", transaction)) {
//            throw new RuntimeException("no privileges");
//        }
//        stationRestUtils.delete(id, transaction);
//    }

    @RequestMapping("user/marketplace")
    public Marketplace getMarketplace(@AuthData User user, Transaction transaction) {
        Long planetId = definePlanetId(user, transaction);
        Query query = transaction.query("select * from ");
        return new Marketplace(null, null, null);
    }

    @Scheduled(interval = 1000/* * 60 * 60*/)
    public void stationUpdate() {
        if(true) {
            return;
        }
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select distinct objects.id, objects.population" +
                    " from objects" +
                    " inner join object_type_description on objects.hull_id = object_type_description.id" +
                    " where object_type_description.type = 'station'");

            List<StarShip> stations = query.executeQuery(new PopulationMapper());
            Query productionQuery = transaction.query("select * from productions where station = :station");
            Query bulkQuery = transaction.query("select * from objects" +
                    " inner join object_type_description on objects.hull_id = object_type_description.id" +
                    " inner join object_type on object_type_description.inventory_type = object_type.id " +
                    " where hull_id = :station");
            for(StarShip station : stations) {
                productionQuery.setLong("station", station.getId());
//                List<Production> productions = productionQuery.executeQuery(new ProductionMapper());

                /*List<Long> bulk =
                System.out.println(productions);*/
            }
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        System.out.println("station update loop");
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

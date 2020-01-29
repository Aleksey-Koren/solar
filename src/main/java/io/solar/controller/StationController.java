package io.solar.controller;

import io.solar.dto.Marketplace;
import io.solar.entity.*;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.mapper.PopulationMapper;
import io.solar.mapper.ProductionMapper;
import io.solar.service.ObjectService;
import io.solar.service.StarShipService;
import io.solar.utils.Option;
import io.solar.utils.Page;
import io.solar.utils.StationRestUtils;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import io.solar.utils.server.controller.Scheduled;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping(value = "station")
@Slf4j
public class StationController {

    private final StationRestUtils stationRestUtils;
    private final StarShipService starShipService;

    public StationController(PlanetController planetsController, ObjectService objectService, StarShipService starShipService) {
        this.stationRestUtils = new StationRestUtils(planetsController, objectService);
        this.starShipService = starShipService;
    }

    @RequestMapping(method = "post")
    public Station save(@RequestBody Station station, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-station", transaction)) {
            throw new RuntimeException("no privileges");
        }
        return stationRestUtils.save(station, transaction);
    }

    @RequestMapping("{id}")
    public Station get(@PathVariable("id") Long id, Transaction transaction) {
        return stationRestUtils.get(id, transaction);
    }


    @RequestMapping
    public Page<Station> getAll(Pageable pageable, Transaction transaction) {
        return stationRestUtils.getAll(pageable, transaction);
    }

    @RequestMapping("utils/dropdown")
    public List<Option> dropdown(Transaction transaction) {
        return getAll(new Pageable(0, 9999999), transaction).getContent()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "{id}", method = "delete")
    public void delete(@PathVariable("id") Long id, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-station", transaction)) {
            throw new RuntimeException("no privileges");
        }
        stationRestUtils.delete(id, transaction);
    }

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
                List<Production> productions = productionQuery.executeQuery(new ProductionMapper());

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
        Long planetId = user.getPlanet();
        if(planetId != null) {
            return planetId;
        }
        if(starShip != null) {
            return starShip.getPlanet();
        } else {
            return 4L;//earth id
        }
    }
    private Long definePlanetId(User user, Transaction transaction) {
        Long planetId = user.getPlanet();
        if(planetId != null) {
            return planetId;
        }
        Optional<StarShip> starShip = starShipService.getActiveShip(user, transaction);
        return definePlanetId(user, starShip.get());
    }


}

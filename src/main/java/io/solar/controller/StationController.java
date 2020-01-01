package io.solar.controller;

import io.solar.dto.Marketplace;
import io.solar.entity.Planet;
import io.solar.entity.Production;
import io.solar.entity.Station;
import io.solar.entity.User;
import io.solar.mapper.ProductionMapper;
import io.solar.mapper.StationMapper;
import io.solar.mapper.TotalMapper;
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
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping(value = "station")
@Slf4j
public class StationController {

    private final StationRestUtils stationRestUtils;

    public StationController(PlanetController planetsController) {
        this.stationRestUtils = new StationRestUtils(planetsController);
    }

    @RequestMapping(method = "post")
    public Station save(@RequestBody Station station, @AuthData User user, Transaction transaction) {
        if (!AuthController.userCan(user, "edit-station")) {
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
        if (!AuthController.userCan(user, "edit-station")) {
            throw new RuntimeException("no privileges");
        }
        stationRestUtils.delete(id, transaction);
    }

    @RequestMapping("user/marketplace")
    public Marketplace getMarketplace(@AuthData User user, Transaction transaction) {
        return new Marketplace(null, null, null);
    }


}

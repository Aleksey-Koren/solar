package io.solar.utils;

import io.solar.controller.PlanetController;
import io.solar.entity.Planet;
import io.solar.entity.Production;
import io.solar.entity.objects.Station;
import io.solar.mapper.ProductionMapper;
import io.solar.mapper.StationMapper;
import io.solar.mapper.TotalMapper;
import io.solar.service.ObjectService;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.Pageable;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class StationRestUtils {

    private final PlanetController planetController;
    private final ObjectService objectService;

    public StationRestUtils(PlanetController planetController, ObjectService objectService) {
        this.planetController = planetController;
        this.objectService = objectService;
    }

    public Station save(Station station, Transaction transaction) {
        boolean isNew = station.getId() == null;

        objectService.save(station, transaction);

        List<Production> production = station.getProductions();
        if (production != null && production.size() > 0) {
            Map<Long, Production> existing;
            if (!isNew) {
                Query prodQuery = transaction.query("select * from productions where station=:station");
                prodQuery.setLong("station", station.getId());
//                existing = prodQuery.executeQuery(new ProductionMapper()).stream().collect(Collectors.toMap(Production::getId, v -> v));
//                existing = prodQuery.executeQuery(new ProductionMapper()).stream().collect(Collectors.toMap(Production::getId, v -> v));
                existing = null;

            } else {
                existing = new HashMap<>();
            }
            Query update = transaction.query("update productions set power = :power, product = :product where station = :station and id = :id ");
            Query insert = transaction.query("insert into productions (power, product, station) values (:power, :product, :station)");
            boolean updateFlag = false;
            boolean insertFlag = false;
            for (Production v : production) {
//                v.setStation(station.getId());
                Query q;
                if (existing.containsKey(v.getId())) {
                    q = update;
                    updateFlag = true;
                    q.setLong("id", v.getId());
                } else {
                    q = insert;
                    insertFlag = true;
                }
                q.setFloat("power", v.getPower());
//                q.setLong("product", v.getProduct());
                q.setLong("station", station.getId());
                q.addBatch();
            }
            if (updateFlag) {
                update.executeBatch();
            }
            if (insertFlag) {
                insert.executeBatch();
            }

            Set<Long> newProduction = production.stream().map(Production::getId).collect(Collectors.toSet());
            List<Long> toDelete = new ArrayList<>();
            for (Map.Entry<Long, Production> entry : existing.entrySet()) {
                if (!newProduction.contains(entry.getKey())) {
                    toDelete.add(entry.getKey());
                }
            }
            if (toDelete.size() > 0) {
                Query delQuery = transaction.query("delete from productions where station = :station and id in ("
                        + toDelete.stream().map(v -> "?").collect(Collectors.joining(",")) + ")");
                int i = 0;
                delQuery.setLong("station", station.getId());
                for (Long id : toDelete) {
                    delQuery.setLong(i + 2, id);
                    i++;
                }
                delQuery.execute();
            }
        } else {
            if (!isNew) {
                Query delProduction = transaction.query("delete from productions where station = :station");
                delProduction.setLong("station", station.getId());
            }
        }
        return station;
    }

    public Station get(Long id, Transaction transaction) {
        Query query = transaction.query("select objects.*, otd.sub_type as type from objects" +
                " inner join object_type_description otd on objects.hull_id = otd.id " +
                "where objects.id = :id and otd.type = 'station'");
        query.setLong("id", id);
//        List<Station> existing = query.executeQuery(new StationMapper());
        List<Station> existing = null;
        appendProductions(existing, transaction);
        return existing.size() == 1 ? existing.get(0) : null;
    }

    private void appendProductions(List<Station> existing, Transaction transaction) {
        if (existing != null && !existing.isEmpty()) {
            Query query = transaction.query("select * from productions where station in (" + existing.stream().map(v -> "?").collect(Collectors.joining(",")) + ")");
            for (int i = 0; i < existing.size(); i++) {
                Station station = existing.get(i);
                query.setLong(i + 1, station.getId());
            }

//            List<Production> productions = query.executeQuery(new ProductionMapper());
            List<Production> productions = null;

//            Map<Long, List<Production>> map = new HashMap<>();
            Map<Long, List<Production>> map = null;

            for (Production p : productions) {
//                List<Production> mapped = map.computeIfAbsent(p.getStation(), k -> new ArrayList<>());
//                mapped.add(p);
            }
            existing.forEach(v -> v.setProductions(map.get(v.getId())));
        }
    }

    public Page<Station> getAll(Pageable pageable, Transaction transaction) {
        Query countQ = transaction.query("select count(1) from objects " +
                " inner join object_type_description on objects.hull_id = object_type_description.id" +
                " where object_type_description.type = 'station'");
        Long count = countQ.executeQuery(new TotalMapper()).get(0);
        if (count == 0) {
            return Page.empty();
        }

        Query query = transaction.query("select * from objects " +
                "inner join object_type_description on objects.hull_id = object_type_description.id" +
                " where object_type_description.type = 'station'" +
                " limit :skip, :pageSize");

        query.setInt("skip", pageable.getPage() * pageable.getPageSize());
        query.setInt("pageSize", pageable.getPageSize());
//        List<Station> existing = query.executeQuery(new StationMapper());
        List<Station> existing = null;


        appendProductions(existing, transaction);

        return new Page<>(existing, count);
    }

    public void delete(Long id, Transaction transaction) {
        Query query = transaction.query("delete from objects where id = :id");
        query.setLong("id", id);
        query.execute();
    }
}

package io.solar.controller;

import io.solar.entity.Planet;
import io.solar.entity.Production;
import io.solar.entity.Station;
import io.solar.entity.User;
import io.solar.mapper.ProductionMapper;
import io.solar.mapper.StationMapper;
import io.solar.mapper.TotalMapper;
import io.solar.utils.Option;
import io.solar.utils.Page;
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

    private final PlanetController planetController;

    public StationController(PlanetController planetsController) {
        this.planetController = planetsController;
    }

    @RequestMapping(method = "post")
    public Station save(@RequestBody Station station, @AuthData User user) {
        if (!AuthController.userCan(user, "edit-station")) {
            throw new RuntimeException("no privileges");
        }
        Transaction transaction = null;
        boolean isNew = station.getId() == null;
        try {
            transaction = Transaction.begin();
            Query save = null;
            if (station.getId() != null) {
                Query query = transaction.query("select * from stations where id = :id");
                query.setLong("id", station.getId());
                List<Station> existing = query.executeQuery(new StationMapper());

                if (existing.size() == 1) {
                    save = transaction.query("UPDATE stations set planet=:planet,population=:population,fraction=:fraction," +
                            "type=:type,title=:title, x = :x, y = :y, aphelion = :aphelion, orbital_period = :orbital_period, angle=:angle where id=:id");
                    save.setLong("id", station.getId());
                } else {
                    log.error("can't find planet with id: " + station.getId());
                }
            } else {
                save = transaction.query("insert into stations (planet,population,fraction,type,title,x,y,aphelion,orbital_period, angle) " +
                        " values (:planet, :population, :fraction, :type, :title, :x, :y, :aphelion, :orbital_period, :angle)");
            }

            if (save != null) {
                save.setLong("planet", station.getPlanetId());
                save.setLong("population", station.getPopulation());
                save.setString("fraction", station.getFraction());
                save.setString("type", station.getType());
                save.setString("title", station.getTitle());
                save.setFloat("x", station.getPlanetId() == null ? station.getX() : null);
                save.setFloat("y", station.getPlanetId() == null ? station.getY() : null);
                save.setFloat("aphelion", station.getPlanetId() != null ? station.getAphelion() : null);
                save.setFloat("angle", station.getPlanetId() != null ? station.getAngle() : null);
                save.setFloat("orbital_period", station.getPlanetId() != null ? station.getOrbitalPeriod() : null);

                save.execute();
                if (station.getId() == null) {
                    station.setId(save.getLastGeneratedKey(Long.class));
                }
            } else {
                throw new RuntimeException("Can't save or update product");
            }

            List<Production> production = station.getProduction();
            if (production != null && production.size() > 0) {
                Map<Long, Production> existing;
                if (!isNew) {
                    Query prodQuery = transaction.query("select * from productions where station=:station");
                    prodQuery.setLong("station", station.getId());
                    existing = prodQuery.executeQuery(new ProductionMapper()).stream().collect(Collectors.toMap(Production::getId, v -> v));
                } else {
                    existing = new HashMap<>();
                }
                Query update = transaction.query("update productions set power = :power, product = :product where station = :station and id = :id ");
                Query insert = transaction.query("insert into productions (power, product, station) values (:power, :product, :station)");
                boolean updateFlag = false;
                boolean insertFlag = false;
                for (Production v : production) {
                    v.setStation(station.getId());
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
                    q.setLong("product", v.getProduct());
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

            transaction.commit();
            return station;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("{id}")
    public Station get(@PathVariable("id") Long id) {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from stations where id = :id");
            query.setLong("id", id);
            List<Station> existing = query.executeQuery(new StationMapper());
            appendPlanets(existing);
            appendProductions(existing);
            transaction.commit();
            return existing.size() == 1 ? existing.get(0) : null;
        } catch (SQLException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


    @RequestMapping
    public Page<Station> getAll(Pageable pageable) {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();

            Query countQ = transaction.query("select count(1) from stations");
            Long count = countQ.executeQuery(new TotalMapper()).get(0);
            if (count == 0) {
                return Page.empty();
            }

            Query query = transaction.query("select * from stations limit :skip, :pageSize");

            query.setInt("skip", pageable.getPage() * pageable.getPageSize());
            query.setInt("pageSize", pageable.getPageSize());
            List<Station> existing = query.executeQuery(new StationMapper());

            appendPlanets(existing);
            appendProductions(existing);


            transaction.commit();
            return new Page<>(existing, count);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private void appendPlanets(List<Station> existing) {
        if (existing != null && !existing.isEmpty()) {
            Map<Long, Planet> planets = planetController.getByIds(existing.stream()
                    .map(Station::getPlanetId)
                    .collect(Collectors.toList())
            ).stream().collect(
                    Collectors.toMap(Planet::getId, v -> v)
            );
            existing.forEach(v -> v.setPlanet(planets.get(v.getPlanetId())));
        }
    }

    private void appendProductions(List<Station> existing) {
        if (existing != null && !existing.isEmpty()) {
            Transaction transaction = null;
            try {
                transaction = Transaction.begin();

                Query query = transaction.query("select * from productions where station in (" + existing.stream().map(v -> "?").collect(Collectors.joining(",")) + ")");
                for (int i = 0; i < existing.size(); i++) {
                    Station station = existing.get(i);
                    query.setLong(i + 1, station.getId());
                }

                List<Production> productions = query.executeQuery(new ProductionMapper());
                Map<Long, List<Production>> map = new HashMap<>();
                for (Production p : productions) {
                    List<Production> mapped = map.computeIfAbsent(p.getStation(), k -> new ArrayList<>());
                    mapped.add(p);
                }
                existing.forEach(v -> v.setProduction(map.get(v.getId())));
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("utils/dropdown")
    public List<Option> dropdown() {
        return getAll(new Pageable(0, 9999999)).getContent()
                .stream()
                .map(v -> new Option(v.getId(), v.getTitle()))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "{id}", method = "delete")
    public void delete(@PathVariable("id") Long id, @AuthData User user) {
        if (!AuthController.userCan(user, "edit-station")) {
            throw new RuntimeException("no privileges");
        }
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("delete from stations where id = :id");
            query.setLong("id", id);
            query.execute();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


}

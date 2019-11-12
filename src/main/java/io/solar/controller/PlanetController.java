package io.solar.controller;

import io.solar.entity.Planet;
import io.solar.entity.User;
import io.solar.mapper.PlanetMapper;
import io.solar.utils.Option;
import io.solar.utils.context.AuthData;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.controller.PathVariable;
import io.solar.utils.server.controller.RequestBody;
import io.solar.utils.server.controller.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping(value = "planet")
@Slf4j
public class PlanetController {


    @RequestMapping(method = "post")
    public Planet save(@RequestBody Planet planet, @AuthData User user) {
        if (!AuthController.userCan(user, "edit-planets")) {
            throw new RuntimeException("no privileges");
        }
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query save = null;
            if (planet.getId() != null) {
                Query query = transaction.query("select * from planets where id = :id");
                query.setLong("id", planet.getId());
                List<Planet> existing = query.executeQuery(new PlanetMapper());
                if (existing.size() == 1) {
                    save = transaction.query("UPDATE planets set aldebo=:aldebo,aphelion=:aphelion,angle=:angle," +
                            "axial_tilt=:axialTilt,eccentricity=:eccentricity,escape_velocity=:escapeVelocity," +
                            "inclination=:inclination,mass=:mass,mean_anomaly=:meanAnomaly,mean_orbit_radius=:meanOrbitRadius," +
                            "mean_radius=:meanRadius,title=:title," +
                            "type=:type,orbital_period=:orbitalPeriod,perihelion=:perihelion," +
                            "sidereal_rotation_period=:siderealRotationPeriod,surface_gravity=:surfaceGravity," +
                            "surface_pressure=:surfacePressure,volume=:volume,parent=:parent where id=:id");
                    save.setLong("id", planet.getId());
                } else {
                    log.error("can't find planet with id: " + planet.getId());
                }
            } else {
                save = transaction.query("insert into planets (aldebo, aphelion, angle, axial_tilt, eccentricity, " +
                        "escape_velocity, inclination, mass, mean_anomaly, mean_orbit_radius, mean_radius, " +
                        "title, type, orbital_period, perihelion, sidereal_rotation_period, surface_gravity," +
                        " surface_pressure, volume, parent) values (:aldebo, :aphelion, :angle, :axialTilt, :eccentricity," +
                        " :escapeVelocity, :inclination, :mass, :meanAnomaly, :meanOrbitRadius, :meanRadius," +
                        " :title, :type, :orbitalPeriod, :perihelion, :siderealRotationPeriod, :surfaceGravity," +
                        " :surfacePressure, :volume, :parent)");
            }
            if (save != null) {
                save.setFloat("aldebo", planet.getAldebo());
                save.setLong("aphelion", planet.getAphelion());
                save.setFloat("angle", planet.getAngle());
                save.setString("axialTilt", planet.getAxialTilt());
                save.setString("eccentricity", planet.getEccentricity());
                save.setString("escapeVelocity", planet.getEscapeVelocity());
                save.setString("inclination", planet.getInclination());
                save.setString("mass", planet.getMass());
                save.setFloat("meanAnomaly", planet.getMeanAnomaly());
                save.setString("meanOrbitRadius", planet.getMeanOrbitRadius());
                save.setString("meanRadius", planet.getMeanRadius());
                save.setString("title", planet.getTitle());
                save.setString("type", planet.getType());
                save.setString("orbitalPeriod", planet.getOrbitalPeriod());
                save.setString("perihelion", planet.getPerihelion());
                save.setString("siderealRotationPeriod", planet.getSiderealRotationPeriod());
                save.setString("surfaceGravity", planet.getSurfaceGravity());
                save.setString("surfacePressure", planet.getSurfacePressure());
                save.setString("volume", planet.getVolume());
                save.setLong("parent", planet.getParent());
                save.execute();
                if (planet.getId() == null) {
                    planet.setId(save.getLastGeneratedKey(Long.class));
                }
            } else {
                throw new RuntimeException("Can't save or update planet");
            }
            transaction.commit();
            return planet;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("{id}")
    public Planet get(@PathVariable("id") Long id) {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from planets where id = :id");
            query.setLong("id", id);
            List<Planet> existing = query.executeQuery(new PlanetMapper());
            transaction.commit();
            return existing.size() == 1 ? existing.get(0) : null;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


    @RequestMapping
    public List<Planet> getAll() {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from planets");
            List<Planet> existing = query.executeQuery(new PlanetMapper());
            transaction.commit();
            return existing;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("utils/dropdown")
    public List<Option> dropdown() {
        return getAll().stream().map(v -> new Option(v.getId(), v.getTitle())).collect(Collectors.toList());
    }

    public List<Planet> getByIds(List<Long> ids) {
        Transaction transaction = null;
        try {
            transaction = Transaction.begin();
            Query query = transaction.query("select * from planets where id in (" +
                    ids.stream().map(v -> "?").collect(Collectors.joining(", ")) + ")");
            for (int i = 0; i < ids.size(); i++) {
                query.setLong(i + 1, ids.get(i));
            }
            List<Planet> existing = query.executeQuery(new PlanetMapper());
            transaction.commit();
            return existing;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }


}

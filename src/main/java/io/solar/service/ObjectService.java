package io.solar.service;

import io.solar.entity.objects.ObjectItem;
import io.solar.entity.objects.Station;
import io.solar.entity.objects.AbstractObject;
import io.solar.mapper.StationMapper;
import io.solar.utils.db.Query;
import io.solar.utils.db.Transaction;
import io.solar.utils.server.beans.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
public class ObjectService {

    public AbstractObject save(AbstractObject object, Transaction transaction) {
        Query save = null;
        if (object.getId() != null) {
            Query query = transaction.query("select * from objects where id = :id");
            query.setLong("id", object.getId());
            List<Station> existing = query.executeQuery(new StationMapper());

            if (existing.size() == 1) {
                save = transaction.query("UPDATE objects set planet=:planet,population=:population,fraction=:fraction," +
                        "title=:title, x = :x, y = :y, aphelion = :aphelion," +
                        " orbital_period = :orbital_period, angle=:angle, user_id = :user_id, hull_id = :hull_id, " +
                        " status = :status, durability = :durability" +
                        " where id=:id");
                save.setLong("id", object.getId());
            } else {
                log.error("can't find planet with id: " + object.getId());
            }
        } else {
            save = transaction.query("insert into objects (planet,population,fraction,title," +
                    "x,y,aphelion,orbital_period, angle, user_id, hull_id, status, durability) " +
                    " values (:planet, :population, :fraction, :title," +
                    " :x, :y, :aphelion, :orbital_period, :angle, :user_id, :hull_id, :status, :durability)");
        }

        if (save != null) {
            save.setLong("planet", object.getPlanet());
            save.setLong("population", object.getPopulation());
            save.setString("fraction", object.getFraction());
            save.setString("title", object.getTitle());
            save.setFloat("x", object.getPlanet() == null ? object.getX() : null);
            save.setFloat("y", object.getPlanet() == null ? object.getY() : null);
            save.setFloat("aphelion", object.getPlanet() != null ? object.getAphelion() : null);
            save.setFloat("angle", object.getPlanet() != null ? object.getAngle() : null);
            save.setFloat("orbital_period", object.getPlanet() != null ? object.getOrbitalPeriod() : null);
            save.setLong("user_id", object.getUserId());
            save.setLong("hull_id", object.getHullId());
            save.setString("status", object.getStatus() != null ? object.getStatus().toString() : null);
            save.setLong("durability", object.getDurability());

            save.execute();
            if (object.getId() == null) {
                object.setId(save.getLastGeneratedKey(Long.class));
            }
        } else {
            throw new RuntimeException("Can't save or update product");
        }

        saveAttachedObjects(object, transaction);

        return object;
    }

    private void saveAttachedObjects(AbstractObject object, Transaction transaction) {
        Query query = transaction.query("update objects set attached_to_ship = null where attached_to_ship = :id");
        query.setLong("id", object.getId());
        query.executeUpdate();
        if(object.getAttachedObjects() == null || object.getAttachedObjects().isEmpty()) {
            return;
        }
        List<ObjectItem> objects = object.getAttachedObjects();
        Query q = transaction.query("update objects" +
                " set attached_to_ship = :attachedToShip, attached_to_socket = :attachedToSocket" +
                " where id = :id");
        for(ObjectItem oi : objects) {
            q.setLong("id", oi.getId());
            q.setLong("attachedToShip", object.getId());
            q.setLong("attachedToSocket", oi.getAttachedToSocket());
            q.addBatch();
        }
        q.executeBatch();
    }
}

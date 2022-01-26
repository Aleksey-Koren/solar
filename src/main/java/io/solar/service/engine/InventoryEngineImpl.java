package io.solar.service.engine;

import io.solar.config.properties.StarShipProperties;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.object.BasicObjectService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InventoryEngineImpl implements InventoryEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final SpaceTechEngine spaceTechEngine;
    private final StarShipProperties starShipProperties;

    @Override
    public int putToInventory(SpaceTech location, List<BasicObject> items) {
        if (spaceTechEngine.isThereEnoughSpaceForObjects(location, items)) {
            items.forEach(s -> {
                s.setAttachedToShip((BasicObject) location);
                s.setX(null);
                s.setY(null);
                s.setStatus(ObjectStatus.ATTACHED_TO);
                s.setAttachedToShip((BasicObject) location);
            });
            basicObjectRepository.saveAll(items);
            return items.size();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no enough space in SpaceTech container to put items in it");
        }
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, BasicObject object) {
        return ship.getId().equals(object.getAttachedToShip().getId());
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, List<BasicObject> objects) {
        return objects.stream().allMatch(obj -> isInShipInventory(ship, obj));
    }

    @Override
    public void moveToMarketplace(BasicObject object) {
        object.setAttachedToShip(null);
        object.setStatus(ObjectStatus.AT_MARKETPLACE);
        basicObjectRepository.save(object);
    }

    @Override
    public void dropToSpace(StarShip starShip, BasicObject object) {
        CoordinatePoint point = generateRandomCoordinatesInDropRadius(starShip);
        setInSpaceParameters(object, point);
    }

    @Override
    public void dropToSpace(StarShip starShip, List<BasicObject> objects) {
        objects.forEach(s -> setInSpaceParameters(s, new CoordinatePoint(randomCoordinateShift(starShip.getX()), randomCoordinateShift(starShip.getY()))));
        basicObjectRepository.saveAllAndFlush(objects);
    }

    private CoordinatePoint generateRandomCoordinatesInDropRadius(StarShip starShip) {
        return new CoordinatePoint(randomCoordinateShift(starShip.getX()), randomCoordinateShift(starShip.getY()));
    }

    private Float randomCoordinateShift(Float value) {
        Random random = new Random();
        boolean positive = random.nextBoolean();
        return positive ? random.nextFloat(value, value + starShipProperties.getDropRadius())
                : random.nextFloat(value, value - starShipProperties.getDropRadius());
    }

    private void setInSpaceParameters(BasicObject object, CoordinatePoint point) {
        object.setX(point.getX());
        object.setY(point.getY());
        object.setAttachedToSocket(null);
        object.setAttachedToShip(null);
        object.setStatus(ObjectStatus.IN_SPACE);
    }

    @Data
    @AllArgsConstructor
    private static class CoordinatePoint {

        private Float x;
        private Float y;
    }
}
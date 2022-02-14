package io.solar.service.engine.inventory;

import io.solar.config.properties.NavigatorProperties;
import io.solar.config.properties.StarShipProperties;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.StarMapEngineImpl;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.NavigationEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class InventoryEngineImpl implements InventoryEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final SpaceTechEngine spaceTechEngine;
    private final StarShipProperties starShipProperties;
    private final NavigationEngine navigationEngine;
    private final NavigatorProperties navigatorProperties;

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
        object.setAttachedToSocket(null);
        object.setStatus(ObjectStatus.AT_MARKETPLACE);
        basicObjectRepository.save(object);
    }

    @Override
    public void dropToSpace(StarShip starShip, BasicObject object) {
        StarMapEngineImpl.CoordinatePoint point = generateRandomCoordinatesInDropRadius(starShip);
        setInSpaceParameters(object, point);
    }

    @Override
    public void dropToSpace(StarShip starShip, List<BasicObject> objects) {
        objects.forEach(s -> setInSpaceParameters(s, generateRandomCoordinatesInDropRadius(starShip)));
        basicObjectRepository.saveAll(objects);
    }

    @Override
    public void dropToSpaceExplosion(StarShip starShip, List<BasicObject> objects) {
        objects.stream()
                .peek(s -> setInSpaceParameters(s, new StarMapEngineImpl.CoordinatePoint(starShip.getX(), starShip.getY())))
                .forEach(s -> navigationEngine.setRandomSpeedInRange(s,
                        navigatorProperties.getExplosionSpeedMin(), navigatorProperties.getExplosionSpeedMax()));
    }

    @Override
    public void putToExchange(StarShip starship, BasicObject inventoryObject) {
        if (!starship.getId().equals(inventoryObject.getAttachedToShip().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Object to put to exchange is not is user's starship");
        }

        inventoryObject.setAttachedToShip(null);
        inventoryObject.setAttachedToSocket(null);
        inventoryObject.setStatus(ObjectStatus.AT_EXCHANGE);
        basicObjectRepository.save(inventoryObject);
    }

    private StarMapEngineImpl.CoordinatePoint generateRandomCoordinatesInDropRadius(StarShip starShip) {
        return new StarMapEngineImpl.CoordinatePoint(randomCoordinateShift(starShip.getX()),
                randomCoordinateShift(starShip.getY()));
    }

    private Double randomCoordinateShift(Double value) {
        Random random = new Random();
        boolean positive = random.nextBoolean();
        return positive ? random.nextDouble(value, value + starShipProperties.getDropRadius())
                : random.nextDouble(value - starShipProperties.getDropRadius(), value );
    }

    private void setInSpaceParameters(BasicObject object, StarMapEngineImpl.CoordinatePoint point) {
        object.setX(point.getX());
        object.setY(point.getY());
        object.setAttachedToSocket(null);
        object.setAttachedToShip(null);
        object.setStatus(ObjectStatus.IN_SPACE);
    }
}
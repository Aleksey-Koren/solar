package io.solar.service.engine.inventory;

import io.solar.config.properties.NavigatorProperties;
import io.solar.config.properties.StarShipProperties;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.multithreading.StationMonitor;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.StarShipService;
import io.solar.service.engine.StarMapEngineImpl;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.NavigationEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.inventory.socket.SpaceTechSocketService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEngineImpl implements InventoryEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final BasicObjectService basicObjectService;
    private final StarShipService starShipService;
    private final SpaceTechEngine spaceTechEngine;
    private final StarShipProperties starShipProperties;
    private final NavigationEngine navigationEngine;
    private final NavigatorProperties navigatorProperties;
    private final SpaceTechSocketService spaceTechSocketService;
    private final StationMonitor stationMonitor;

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
    public boolean isInSpaceTechInventory(BasicObject ship, BasicObject object) {
        return ship.getId().equals(object.getAttachedToShip().getId());
    }

    @Override
    public boolean isInSpaceTechInventory(BasicObject ship, List<BasicObject> objects) {
        return objects.stream().allMatch(obj -> isInSpaceTechInventory(ship, obj));
    }

    @Override
    public void moveToMarketplace(BasicObject object) {
        object.setAttachedToShip(null);
        object.setAttachedToSocket(null);
        object.setStatus(ObjectStatus.AT_MARKETPLACE);
        basicObjectRepository.save(object);
    }

    @Override
    public void dropToSpaceExplosion(StarShip starShip, List<BasicObject> objects) {
        objects.stream()
                .peek(s -> setInSpaceParameters(s, new StarMapEngineImpl.CoordinatePoint(starShip.getX(), starShip.getY())))
                .forEach(s -> navigationEngine.setRandomSpeedInRange(s,
                        navigatorProperties.getExplosionSpeedMin(), navigatorProperties.getExplosionSpeedMax()));
    }

    private void setInSpaceParameters(BasicObject object, StarMapEngineImpl.CoordinatePoint point) {
        object.setX(point.getX());
        object.setY(point.getY());
        object.setAttachedToSocket(null);
        object.setAttachedToShip(null);
        object.setStatus(ObjectStatus.IN_SPACE);
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

    @Override
    public void moveFromOwnerToStation(List<BasicObject> items, User user, Station station) {
        synchronized (stationMonitor.getMonitor(station.getId())) {
            checkOwnerToStation(user, station, items);
            moveItemsTo(items, station);
        }
    }

    @Override
    public void moveFromStationToOwner(List<BasicObject> items, User user, Station station) {
        synchronized (stationMonitor.getMonitor(station.getId())) {
            BasicObject starShip = user.getLocation();
            checkStationToOwner(user, station, items);
            moveItemsTo(items, starShip);
        }
    }

    private void checkOwnerToStation(User user, Station station, List<BasicObject> items) {
        checkOwnerToStationCommon(user,station,items);
        String message = "Impossible to put item to station inventory. Reason: {}";

        items.forEach(s -> {
            if(!isInSpaceTechInventory(user.getLocation(), s)) {
                String reason = String.format("Item with id = %d is not in user's location-ship id = %d inventory", s.getId(), station.getId());
                log.warn(message, reason);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
            }
        });

        if (!spaceTechEngine.isThereEnoughSpaceForObjects(station, items)) {
            String reason = String.format("There is not enough space for new objects in station id = %d", station.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }

    private void checkStationToOwner(User user, Station station, List<BasicObject> items) {
        checkOwnerToStationCommon(user,station,items);
        String message = "Impossible to put item to station inventory. Reason: {}";
        items.forEach(s -> {
            if(!isInSpaceTechInventory(station, s)) {
                String reason = String.format("Item with id = %d is not in station id = %d inventory", s.getId(), station.getId());
                log.warn(message, reason);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
            }
        });

        StarShip starShip = starShipService.getById(user.getLocation().getId());
        if (!spaceTechEngine.isThereEnoughSpaceForObjects(starShip, items)) {
            String reason = String.format("There is not enough space for new objects in starShip id = %d", starShip.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

    }

    private void checkOwnerToStationCommon(User user, Station station, List<BasicObject> items) {
        String message = "Impossible to put item to station inventory. Reason: {}";

        if(!spaceTechEngine.isUserOwnsThisSpaceTech(user, station)) {
            String reason = String.format("User with id = %d is not owner of station id = %d", user.getId(), station.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }

        if(!spaceTechEngine.isUserAtStation(user, station)) {
            String reason = String.format("User with id = %d is not at station id = %d", user.getId(), station.getId());
            log.warn(message, reason);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
        }
    }

    private void moveItemsTo(List<BasicObject> items, BasicObject spaceTech) {
        for(BasicObject item : items) {
            Optional<SpaceTechSocket> socketOpt = spaceTechSocketService.findByObject(item);
            if (socketOpt.isPresent()) {
                SpaceTechSocket socket = socketOpt.get();
                socket.detachItem();
                spaceTechSocketService.save(socket);
            }
            item.setAttachedToShip(spaceTech);
            spaceTech.getAttachedObjects().add(item);
        }

        basicObjectService.saveAll(items);
    }
}
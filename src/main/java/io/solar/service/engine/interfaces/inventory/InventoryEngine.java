package io.solar.service.engine.interfaces.inventory;

import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;

import java.util.List;

public interface InventoryEngine {

    int putToInventory(SpaceTech location, List<BasicObject> items);

    boolean isInSpaceTechInventory(BasicObject ship, BasicObject object);

    void moveToMarketplace(BasicObject object);

    boolean isInSpaceTechInventory(BasicObject ship, List<BasicObject> objects);

    void dropToSpaceExplosion(StarShip starShip, List<BasicObject> objects);

    void putToExchange(StarShip starship, BasicObject inventoryObject);

    void moveFromOwnerToStation(List<BasicObject> items, User user, Station station);

    void moveFromStationToOwner(List<BasicObject> items, User user, Station station);
}
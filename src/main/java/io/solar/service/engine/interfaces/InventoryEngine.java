package io.solar.service.engine.interfaces;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;

import java.util.List;

public interface InventoryEngine {

    int putToInventory(SpaceTech location, List<BasicObject> items);

    boolean isInShipInventory(BasicObject ship, BasicObject object);

    void moveToMarketplace(BasicObject object);

    boolean isInShipInventory(BasicObject ship, List<BasicObject> objects);

    void dropToSpace(StarShip starShip, BasicObject object);

    void dropToSpace(StarShip starShip, List<BasicObject> objects);

    void putToExchange(StarShip starship, BasicObject inventoryObject);
}
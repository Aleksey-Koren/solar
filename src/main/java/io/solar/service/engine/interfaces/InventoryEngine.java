package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;

import java.util.List;

public interface InventoryEngine {

    int putToInventory(BasicObject location, List<BasicObject> items);

    boolean isInShipInventory(BasicObject ship, BasicObject object);

    void moveToMarketplace(BasicObject object);

    boolean isInShipInventory(BasicObject ship, List<BasicObject> objects);
}
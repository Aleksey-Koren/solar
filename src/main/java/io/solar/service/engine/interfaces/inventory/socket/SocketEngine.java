package io.solar.service.engine.interfaces.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.objects.BasicObject;

import java.util.Optional;

public interface SocketEngine {

    void attachToSocket(InventorySocket inventorySocket, BasicObject spaceTech, BasicObject object);

    Optional<BasicObject> hasAttachedObject(Long SocketId, SpaceTech spaceTech);

    void detachFromSocket(BasicObject object);
}
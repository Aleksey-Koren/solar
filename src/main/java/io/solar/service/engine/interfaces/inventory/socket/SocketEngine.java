package io.solar.service.engine.interfaces.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;

import java.util.Optional;

public interface SocketEngine {

    void attachToSocket(Long SocketId, long ObjectId);

    Optional<BasicObject> hasAttachedObject(Long SocketId, SpaceTech spaceTech);

    void detachFromSocket(BasicObject object);

    void attachToSocket(Long socketId, BasicObject object);
}
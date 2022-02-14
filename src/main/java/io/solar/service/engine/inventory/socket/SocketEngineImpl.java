package io.solar.service.engine.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.inventory.socket.SocketEngine;

import java.util.Optional;

public class SocketEngineImpl implements SocketEngine {

    @Override
    public void attachToSocket(Long SocketId, long ObjectId) {

    }

    @Override
    public Optional<BasicObject> hasAttachedObject(Long socketId, SpaceTech spaceTech) {
        return spaceTech.getAttachedObjects().stream()
                .filter(s -> socketId.equals(s.getAttachedToSocket()))
                .findAny();
    }

    @Override
    public void detachFromSocket(BasicObject object) {
        object.setAttachedToSocket(null);
        object.setEnabled(false);
    }

    @Override
    public void attachToSocket(Long socketId, BasicObject object) {
        object.setAttachedToSocket(socketId);
    }
}
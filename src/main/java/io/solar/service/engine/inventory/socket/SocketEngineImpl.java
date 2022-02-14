package io.solar.service.engine.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.inventory.socket.SocketEngine;
import io.solar.service.inventory.socket.SpaceTechSocketService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SocketEngineImpl implements SocketEngine {

    private final SpaceTechSocketService spaceTechSocketService;
    private final BasicObjectService basicObjectService;

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
        object.setIsEnabled(false);

        basicObjectService.save(object);
    }

    @Override
    public void attachToSocket(Long socketId, BasicObject object) {
        object.setAttachedToSocket(socketId);
    }
}
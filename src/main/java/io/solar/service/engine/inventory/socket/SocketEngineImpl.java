package io.solar.service.engine.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.inventory.socket.SocketEngine;
import io.solar.service.inventory.socket.SpaceTechSocketService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SocketEngineImpl implements SocketEngine {

    private final SpaceTechSocketService spaceTechSocketService;
    private final BasicObjectService basicObjectService;

    @Override
    public void attachToSocket(InventorySocket inventorySocket, BasicObject spaceTech, BasicObject item) {
        SpaceTechSocket socketAtSpaceTech = spaceTechSocketService.getBySpaceTechAndInventorySocket(spaceTech, inventorySocket);
        socketAtSpaceTech.attachItem(item);
        spaceTechSocketService.save(socketAtSpaceTech);
        basicObjectService.save(item);
    }

    @Override
    public Optional<BasicObject> hasAttachedObject(Long socketId, SpaceTech spaceTech) {
        return spaceTech.getAttachedObjects().stream()
                .filter(s -> socketId.equals(s.getAttachedToSocket()))
                .findAny();
    }

    @Override
    public void detachFromSocket(BasicObject item) {
        item.setIsEnabled(false);
        SpaceTechSocket socketWithItem = spaceTechSocketService.getByObject(item);
        socketWithItem.detachItem();
        basicObjectService.save(item);
        spaceTechSocketService.save(socketWithItem);
    }
}
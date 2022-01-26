package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.StarShipEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StarShipEngineImpl implements StarShipEngine {

    private final InventoryEngine inventoryEngine;
    private final StarShipService starShipService;

    @Override
    public void explosion(StarShip starship) {
        List<BasicObject> attachedObjects = starship.getAttachedObjects();
        destroyRandomObjects(attachedObjects);
        inventoryEngine.dropToSpace(starship, attachedObjects);
        starShipService.delete(starship);
    }

    private void destroyRandomObjects(List<BasicObject> objects) {
        objects.stream().filter(s -> isExplosed()).forEach(objects::remove);
    }

    private boolean isExplosed() {
        return (Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) / 6 > 0.5;
    }
}
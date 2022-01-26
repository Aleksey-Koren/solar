package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.StarShipEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class StarShipEngineImpl implements StarShipEngine {

    private final InventoryEngine inventoryEngine;
    private final StarShipService starShipService;
    private final BasicObjectService basicObjectService;

    @Override
    public void blowUp(StarShip starship) {
        List<BasicObject> attachedObjects = starship.getAttachedObjects();
        destroyRandomObjects(attachedObjects);
        damageObjects(attachedObjects);
        inventoryEngine.dropToSpace(starship, attachedObjects);
        starShipService.delete(starship);
    }

    private void destroyRandomObjects(List<BasicObject> objects) {
        List<BasicObject> objectsToRemove = objects.stream().filter(s -> isExplosed()).toList();
        objectsToRemove.forEach(objects::remove);
        basicObjectService.deleteAll(objectsToRemove);
    }

    private boolean isExplosed() {
        return (Math.random() + Math.random() + Math.random() + Math.random() + Math.random() + Math.random()) / 6 > 0.5;
    }

    private void damageObjects(List<BasicObject> objects) {
        objects.forEach(s -> s.setDurability(Math.round(s.getDurability() * new Random().nextFloat(0.1f, 0.9f))));
    }
}
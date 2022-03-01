package io.solar.service.engine;

import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.StarMapEngine;
import io.solar.service.engine.interfaces.StarShipEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class StarShipEngineImpl implements StarShipEngine {

    @Value("${app.star_map.min_percent_radius_for_orbit}")
    private Integer minPercentOfRadius;

    @Value("${app.star_map.max_percent_radius_for_orbit}")
    private Integer maxPercentOfRadius;

    private final InventoryEngine inventoryEngine;
    private final StarMapEngine starMapEngine;
    private final StarShipService starShipService;
    private final BasicObjectService basicObjectService;

    @Override
    public void blowUp(StarShip starship) {
        List<BasicObject> attachedObjects = starship.getAttachedObjects();
        destroyRandomObjects(attachedObjects);
        damageObjects(attachedObjects);
        inventoryEngine.dropToSpaceExplosion(starship, attachedObjects);
        starShipService.delete(starship);
    }

    @Override
    public boolean isShipCanDockOrbit(StarShip starShip, Planet planet) {
        int planetRadius = Integer.parseInt(planet.getMeanRadius());
        float distance = starMapEngine.calculateDistanceBetweenObjects(starShip, planet);

        float minDistance = planetRadius * (minPercentOfRadius / 100f);
        float maxDistance = planetRadius * (maxPercentOfRadius / 100f);

        return distance >= minDistance && distance <= maxDistance;
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
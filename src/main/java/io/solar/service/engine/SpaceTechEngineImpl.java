package io.solar.service.engine;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.InventoryTypeRepository;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.inventory.InventoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class SpaceTechEngineImpl implements SpaceTechEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final InventoryTypeRepository objectTypeRepository;
    private final InventoryTypeService inventoryTypeService;

    @Override
    public Float retrieveViewDistance(SpaceTech spaceTech) {
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;

        List<BasicObject> radars = basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), objectTypeRepository.findByTitle("radar")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't find type with title [%s]", "radar"))));

        double distance = radars.stream()
                .map(s -> s.getObjectTypeDescription().getDistance()).mapToDouble(Float::doubleValue)
                .distinct()
                .max()
                .orElse(0);
        return (float) distance;
    }

    @Override
    public Integer calculateMass(SpaceTech spaceTech) {
        BasicObject spaceTechObject = (BasicObject) spaceTech;

        int starshipMass = spaceTechObject.getObjectTypeDescription().getMass();

        int attachedObjectsMass = spaceTechObject.getAttachedObjects()
                .stream()
                .mapToInt(object -> object.getObjectTypeDescription().getMass())
                .sum();

//        return starshipMass + attachedObjectsMass;
        return 100;
    }

    @Override
    public float calculateMaxThrust(SpaceTech spaceTech) {
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        List<BasicObject> engines = basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), objectTypeRepository.findByTitle("engine")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't find type with title [%s]", "engine")))
        );

        engines.addAll(basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), objectTypeRepository.findByTitle("huge_engine")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Can't find type with title [%s]", "huge_engine")))
        ));

        return (float) engines.stream()
                .mapToDouble(s -> (double) s.getObjectTypeDescription().getPowerMax())
                .sum();
    }

    /**
     * ObjectTypeDescription.powerMin is container capacity
     */
    @Override
    public float calculateSpaceTechVolume(SpaceTech spaceTech) {
        BasicObject ship = (BasicObject) spaceTech;

        InventoryType container = inventoryTypeService.getByTitle("container");

        return (float) ship.getAttachedObjects().stream()
                .filter(object -> object.getObjectTypeDescription().getInventoryType().equals(container))
                .mapToDouble(object -> object.getObjectTypeDescription().getPowerMin())
                .sum();
    }

    @Override
    public boolean isThereEnoughSpaceForObjects(SpaceTech spaceTech, List<BasicObject> objects) {
        BasicObject object = (BasicObject) spaceTech;

        float shipVolume = calculateSpaceTechVolume(spaceTech);

        double objectsVolume = objects.stream()
                .mapToDouble(BasicObject::getVolume)
                .sum();

        double currentUsedVolume = object.getAttachedObjects()
                .stream()
                .mapToDouble(BasicObject::getVolume)
                .sum();

        double goodsVolume = spaceTech.getGoods()
                .stream()
                .mapToDouble(goods -> goods.getProduct().getVolume() * goods.getAmount())
                .sum();

        return ((currentUsedVolume + objectsVolume + goodsVolume) < shipVolume);
    }

    @Override
    public float calculateMaxAcceleration(SpaceTech spaceTech) {
        return calculateMaxThrust(spaceTech) / calculateMass(spaceTech);
    }
}
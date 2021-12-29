package io.solar.service.engine;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.InventoryTypeRepository;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SpaceTechEngineImpl implements SpaceTechEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final InventoryTypeRepository objectTypeRepository;

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

    public Integer calculateMass(SpaceTech spaceTech) {
        BasicObject spaceTechObject = (BasicObject) spaceTech;

        int starshipMass = spaceTechObject.getObjectTypeDescription().getMass();

        int attachedObjectsMass = spaceTechObject.getAttachedObjects()
                .stream()
                .mapToInt(object -> object.getObjectTypeDescription().getMass())
                .sum();

        return starshipMass + attachedObjectsMass;
    }

    public float calculateMaxAcceleration(SpaceTech spaceTech) {
        return calculateMaxThrust(spaceTech) / calculateMass(spaceTech);
    }

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
}
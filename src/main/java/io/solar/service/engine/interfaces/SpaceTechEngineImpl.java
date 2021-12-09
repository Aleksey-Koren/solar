package io.solar.service.engine.interfaces;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SpaceTechEngineImpl implements SpaceTechEngine {

    private final BasicObjectRepository basicObjectRepository;

    @Override
    public Float retrieveViewDistance(SpaceTech spaceTech) {
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        List<BasicObject> radars = basicObjectRepository.getObjectsInSlotsByTypeId(spaceTechAsObject.getId(), 7L);
        Double distance = radars.stream()
                .map(s -> s.getObjectTypeDescription().getDistance()).mapToDouble(Float::doubleValue)
                .distinct()
                .max()
                .orElse(0);
        return distance.floatValue();
    }
}

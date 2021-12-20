package io.solar.service;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StarMapService {

    private final SpaceTechEngine spaceTechEngine;
    private final BasicObjectRepository basicObjectRepository;

    public List<BasicObject> findAllInViewDistance(SpaceTech spaceTech) {
        Float viewDistance = spaceTechEngine.retrieveViewDistance(spaceTech);
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        if(viewDistance > 0) {
            return basicObjectRepository.findAllInViewDistance(spaceTechAsObject.getX(), spaceTechAsObject.getY(), viewDistance);
        }else{
            return basicObjectRepository.findAllWithZeroViewDistance(spaceTechAsObject.getX(), spaceTechAsObject.getY(), 100f);
        }
    }
}
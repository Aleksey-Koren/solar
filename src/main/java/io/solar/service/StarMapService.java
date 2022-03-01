package io.solar.service;

import io.solar.config.properties.AppProperties;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.engine.interfaces.StarMapEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StarMapService {

    @Value("${app.star_map.capture_distance}")
    private Float captureDistance;

    private final SpaceTechEngine spaceTechEngine;
    private final StarMapEngine starMapEngine;
    private final BasicObjectRepository basicObjectRepository;
    private final AppProperties appProperties;

    public List<BasicObject> findAllInViewDistance(SpaceTech spaceTech) {
        Double viewDistance = spaceTechEngine.retrieveViewDistance(spaceTech);
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        if (viewDistance > 0) {
            return basicObjectRepository.findAllInViewDistance(spaceTechAsObject.getX(), spaceTechAsObject.getY(), viewDistance);
        } else {
            return basicObjectRepository.findAllInViewDistance(spaceTechAsObject.getX(), spaceTechAsObject.getY(), appProperties.getViewDistanceWithoutRadar());
        }
    }

    public boolean isShipCanPickUpObject(StarShip ship, BasicObject object) {
        float distanceBetweenObjects = starMapEngine.calculateDistanceBetweenObjects(ship, object);

        return distanceBetweenObjects <= captureDistance;
    }
}
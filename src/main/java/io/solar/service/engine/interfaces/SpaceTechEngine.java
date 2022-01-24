package io.solar.service.engine.interfaces;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;

import java.util.List;

public interface SpaceTechEngine {

    Float retrieveViewDistance(SpaceTech spaceTech);

    Integer calculateMass(SpaceTech spaceTech);

    float calculateMaxThrust(SpaceTech spaceTech);

    float calculateMaxAcceleration(SpaceTech spaceTech);

    float calculateSpaceTechVolume(SpaceTech spaceTech);

    boolean isThereEnoughSpaceForObjects(SpaceTech ship, List<BasicObject> objects);
}

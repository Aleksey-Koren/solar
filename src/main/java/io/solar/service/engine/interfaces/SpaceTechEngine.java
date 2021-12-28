package io.solar.service.engine.interfaces;

import io.solar.entity.interfaces.SpaceTech;

public interface SpaceTechEngine {

    Float retrieveViewDistance(SpaceTech spaceTech);

    Integer calculateMass(SpaceTech spaceTech);
}

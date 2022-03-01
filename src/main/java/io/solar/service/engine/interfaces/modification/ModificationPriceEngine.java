package io.solar.service.engine.interfaces.modification;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.modification.ModificationPrice;

public interface ModificationPriceEngine {

    boolean isEnoughResources(SpaceTech spaceTech, ModificationPrice modificationPrice);
}

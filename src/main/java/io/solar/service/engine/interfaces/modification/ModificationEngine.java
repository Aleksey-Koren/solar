package io.solar.service.engine.interfaces.modification;

import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.objects.BasicObject;

public interface ModificationEngine {

    void applyModification(BasicObject object, ModificationPrice modificationPrice);
}

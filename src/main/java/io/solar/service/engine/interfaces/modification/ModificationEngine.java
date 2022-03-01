package io.solar.service.engine.interfaces.modification;

import io.solar.entity.modification.Modification;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;

public interface ModificationEngine {

    void applyModification(BasicObject object, Modification modification);

    boolean isStationAbleToModify(Modification modification, Station station);

    boolean isPossibleToApplyToItem(Modification modification, BasicObject item);
}
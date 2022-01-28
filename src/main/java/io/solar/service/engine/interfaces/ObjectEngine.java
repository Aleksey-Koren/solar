package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.StarShip;

import java.util.List;

public interface ObjectEngine {

    BasicObject createInventoryObjects(ObjectTypeDescription otd);

    List<BasicObject> createInventoryObjects(ObjectTypeDescription otd, int quantity);

    StarShip createStarship(ObjectTypeDescription otd);

    boolean isObjectAStarship(BasicObject object);
}

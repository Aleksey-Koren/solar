package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;

import java.util.List;

public interface ObjectEngine {

    BasicObject createInventoryObject(ObjectTypeDescription otd);

    List<BasicObject> createInventoryObject(ObjectTypeDescription otd, int quantity);
}

package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;

public interface ObjectEngine {

    BasicObject createInventoryObject(ObjectTypeDescription objectTypeDescription);
}

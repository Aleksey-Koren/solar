package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;

public interface NavigationEngine {

    void setRandomSpeedInRange(BasicObject basicObject, Float speedMin, Float speedMax);
}

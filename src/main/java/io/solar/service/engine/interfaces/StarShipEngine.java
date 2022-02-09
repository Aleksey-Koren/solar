package io.solar.service.engine.interfaces;

import io.solar.entity.Planet;
import io.solar.entity.objects.StarShip;

public interface StarShipEngine {

    void blowUp(StarShip starship);

    boolean isShipCanDockOrbit(StarShip starShip, Planet planet);
}

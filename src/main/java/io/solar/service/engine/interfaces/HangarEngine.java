package io.solar.service.engine.interfaces;

import io.solar.entity.User;
import io.solar.entity.objects.StarShip;

public interface HangarEngine {

    void moveToMarketplace(StarShip starship, User user);

    boolean isUserAndShipAreInTheSameHangar(User user, StarShip starShip);

    void boardStarShip(StarShip starShip, User user);
}

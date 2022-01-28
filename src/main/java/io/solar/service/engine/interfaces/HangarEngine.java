package io.solar.service.engine.interfaces;

import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;

public interface HangarEngine {

    void moveToMarketplace(StarShip starship, User user);

    boolean isUserAndShipAreInTheSameHangar(User user, StarShip starShip);

    void boardStarShip(StarShip starShip, User user);

    void moveToHangar(User user, StarShip starShip, Station station);

    boolean isEnoughSpaceForShipAtStation(User user, Station station);
}

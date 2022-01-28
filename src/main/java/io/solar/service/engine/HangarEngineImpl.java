package io.solar.service.engine;

import io.solar.config.properties.AppProperties;
import io.solar.entity.User;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.HangarEngine;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HangarEngineImpl implements HangarEngine {

    private final UserService userService;
    private final AppProperties appProperties;
    private final StarShipService starShipService;

    @Override
    public void moveToMarketplace(StarShip starship, User user) {
        starship.setAttachedToShip(null);
        starship.setStatus(ObjectStatus.AT_MARKETPLACE);
    }

    @Override
    public boolean isUserAndShipAreInTheSameHangar(User user, StarShip starShip) {
        return user.getLocation().getAttachedToShip().equals(starShip.getAttachedToShip());
    }

    public boolean isItEnoughSpaceForeShipAtThisStation(User user, Station station, StarShip starShip) {
        return starShipService.findAllUserStarshipsInHangar(user, starShip, station).size() <= appProperties.getBasicHangarSize();
    }

    @Override
    public void boardStarShip(StarShip starShip, User user) {
        user.setLocation(starShip);
        userService.save(user);
    }

}
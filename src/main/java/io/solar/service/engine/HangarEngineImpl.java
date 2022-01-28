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
        return user.getLocation().getAttachedToShip().getId().equals(starShip.getAttachedToShip().getId());
    }

    @Override
    public void boardStarShip(StarShip starShip, User user) {
        user.setLocation(starShip);
        userService.save(user);
    }

    @Override
    public void moveToHangar(User user, StarShip starShip, Station station) {
        if (isEnoughSpaceForShipAtStation(user, station)) {
            starShip.setStatus(ObjectStatus.ATTACHED_TO);
            starShip.setAttachedToShip(station);
            starShip.setUser(user);

            starShipService.save(starShip);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough space for ship in hangar");
        }
    }

    @Override
    public boolean isEnoughSpaceForShipAtStation(User user, Station station) {
        return starShipService.findAllUserStarshipsInHangar(user, station).size() < appProperties.getHangarShipAmount();
    }
}
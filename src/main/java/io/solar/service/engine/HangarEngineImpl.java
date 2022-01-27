package io.solar.service.engine;

import io.solar.entity.User;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.HangarEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HangarEngineImpl implements HangarEngine {

    private final UserService userService;

    @Override
    public void moveToMarketplace(StarShip starship, User user) {
        starship.setAttachedToShip(null);
        starship.setStatus(ObjectStatus.AT_MARKETPLACE);
    }

    @Override
    public boolean isUserAndShipAreInTheSameHangar(User user, StarShip starShip) {
        return user.getLocation().getAttachedToShip().equals(starShip.getAttachedToShip());
    }

    @Override
    public void boardStarShip(StarShip starShip, User user) {
        user.setLocation(starShip);
        userService.save(user);
    }

//    @Override
//    public void moveToHangar(StarShip starShip) {
//        if (isThereEnoughtSpaseInHangar) {
//
//        }
//    }
}

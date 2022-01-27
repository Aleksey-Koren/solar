package io.solar.service.engine;

import io.solar.entity.User;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.StarShip;
import io.solar.service.engine.interfaces.HangarEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HangarEngineImpl implements HangarEngine {

    @Override
    public void moveToMarketplace(StarShip starship, User user) {
        starship.setAttachedToShip(null);
        starship.setStatus(ObjectStatus.AT_MARKETPLACE);
    }
}

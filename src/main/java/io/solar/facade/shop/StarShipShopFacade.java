package io.solar.facade.shop;

import io.solar.dto.shop.ShopDto;
import io.solar.entity.User;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.facade.UserFacade;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.engine.interfaces.HangarEngine;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.object.ObjectTypeDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class StarShipShopFacade {

    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final UserFacade userFacade;
    private final ObjectEngine objectEngine;
    private final HangarEngine hangarEngine;
    private final StationService stationService;

    public void buyStarShip(User user, ShopDto shopDto) {
        Station currentStation = stationService.getById(user.getLocation().getAttachedToShip().getId());

        if (!hangarEngine.isEnoughSpaceForShipAtStation(user, currentStation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't buy Starship. There in no place in hangar");
        }

        ObjectTypeDescription otd = objectTypeDescriptionService.getById(shopDto.getOtdId());
        userFacade.decreaseUserBalance(user, otd.getPrice().longValue());
        StarShip newStarShip = objectEngine.createStarship(otd);
        hangarEngine.moveToHangar(user, newStarShip, currentStation);
    }

}
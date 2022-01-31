package io.solar.facade.shop;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.shop.StarshipPriceDto;
import io.solar.entity.User;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.facade.UserFacade;
import io.solar.repository.UserRepository;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.HangarEngine;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.object.BasicObjectService;
import io.solar.service.object.ObjectTypeDescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StarShipShopFacade {

    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final UserFacade userFacade;
    private final InventoryShopFacade inventoryShopFacade;
    private final ObjectEngine objectEngine;
    private final HangarEngine hangarEngine;
    private final SpaceTechEngine spaceTechEngine;
    private final StationService stationService;
    private final StarShipService starShipService;
    private final BasicObjectService basicObjectService;
    private final UserRepository userRepository;

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

    public void sellStarship(User user, Long starshipId) {
        StarShip starship = starShipService.getById(starshipId);
        if (spaceTechEngine.isUserOwnsThisSpaceTech(user, starship) && hangarEngine.isUserAndShipAreInTheSameHangar(user, starship)) {
            long starshipPrice = calculateStarshipPrice(starship);
            userFacade.increaseUserBalance(user, starshipPrice);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Fail to get object price because user isn't owner or user not locate at the station");
        }
    }



    public List<StarshipPriceDto> getSellPrices(User user, ShopDto shopDto) {
        List<StarShip> starShips = starShipService.findAllStarshipsById(shopDto.getObjectIds());
        boolean isUserOwnerAndUserAndStarshipsAtStation = starShips.stream().allMatch(starship ->
                spaceTechEngine.isUserOwnsThisSpaceTech(user, starship)
                        && hangarEngine.isUserAndShipAreInTheSameHangar(user, starship)
        );

        if (isUserOwnerAndUserAndStarshipsAtStation) {
            return starShips.stream()
                    .map(starship -> new StarshipPriceDto(starship.getId(), calculateStarshipPrice(starship)))
                    .toList();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User isn't owner given starships or user not locate at the station");
        }
    }

    private long calculateStarshipPrice(StarShip starShip) {
        long starshipPrice = inventoryShopFacade.calculateSellPrice(starShip);

        long attachedObjectsPrice = starShip.getAttachedObjects()
                .stream()
                .mapToLong(inventoryShopFacade::calculateSellPrice)
                .sum();

        return starshipPrice + attachedObjectsPrice;


    }

}
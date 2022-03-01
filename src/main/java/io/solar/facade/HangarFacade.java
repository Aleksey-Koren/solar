package io.solar.facade;

import io.solar.dto.object.StarShipDto;
import io.solar.dto.object.StarshipObjectsDto;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.mapper.StarShipMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.engine.interfaces.HangarEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HangarFacade {

    private final BasicObjectService basicObjectService;
    private final StarShipService starShipService;
    private final StarShipMapper starShipMapper;
    private final StationService stationService;
    private final SpaceTechEngine spaceTechEngine;
    private final HangarEngine hangarEngine;
    private final InventoryEngine inventoryEngine;

    public List<StarShipDto> getAllStarships(Long stationId, User user) {
        Station station = stationService.getById(stationId);

        return starShipService.findAllUserStarshipsInHangar(user, station)
                .stream()
                .map(starShipMapper::toDto)
                .toList();
    }

    public HttpStatus boardStarShip(Long starshipId, User user) {
        StarShip starShip = starShipService.getById(starshipId);
        if (!hangarEngine.isUserAndShipAreInTheSameHangar(user, starShip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User can't board this ship because they are not at the same station");
        }
        if (!spaceTechEngine.isUserOwnsThisSpaceTech(user, starShip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User can't board this ship because ship isn't his");
        }

        hangarEngine.boardStarShip(starShip, user);

        return HttpStatus.OK;
    }

    public void moveObjects(Long destStarshipId, User user, StarshipObjectsDto starshipObjectsDto) {
        StarShip sourceStarship = starShipService.getById(starshipObjectsDto.getStarshipId());
        StarShip destStarship = starShipService.getById(destStarshipId);
        List<BasicObject> objectsToMove = basicObjectService.findAllById(starshipObjectsDto.getObjectsIdsToMove());

        if (!spaceTechEngine.isUserOwnsThisSpaceTech(user, sourceStarship)
                || !spaceTechEngine.isUserOwnsThisSpaceTech(user, destStarship)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot move objects because ship isn't his");
        }

        if (!hangarEngine.isUserAndShipAreInTheSameHangar(user, sourceStarship)
                || !hangarEngine.isUserAndShipAreInTheSameHangar(user, destStarship)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User cannot move objects from starship with id = " + sourceStarship.getId());
        }

        if (!inventoryEngine.isInSpaceTechInventory(sourceStarship, objectsToMove)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "There is an alien object among objects to move");
        }

        inventoryEngine.putToInventory(destStarship, objectsToMove);
    }
}
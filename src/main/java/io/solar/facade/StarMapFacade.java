package io.solar.facade;

import io.solar.dto.object.BasicObjectViewDto;
import io.solar.entity.User;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.mapper.object.BasicObjectViewMapper;
import io.solar.service.StarMapService;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StarMapFacade {

    private final StarMapService starMapService;
    private final StarShipService starshipService;
    private final BasicObjectService basicObjectService;
    private final BasicObjectViewMapper basicObjectViewMapperMapper;
    private final InventoryEngine inventoryEngine;

    public List<BasicObjectViewDto> getStarshipView(StarShip starShip) {
        return starMapService.findAllInViewDistance(starShip).stream()
                .map(basicObjectViewMapperMapper::toDto)
                .collect(Collectors.toList());
    }

    public HttpStatus pickUpObject(User user, Long objectId) {
        BasicObject object = basicObjectService.getById(objectId);
        StarShip userShip = starshipService.getById(user.getLocation().getId());

        if (starMapService.isShipCanPickUpObject(userShip, object)) {
            inventoryEngine.putToInventory(userShip, List.of(object));
        } else {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.OK;
    }
}
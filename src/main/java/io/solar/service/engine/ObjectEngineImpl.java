package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.ObjectType;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.StarShip;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ObjectEngineImpl implements ObjectEngine {

    private final BasicObjectService basicObjectService;
    private final StarShipService starShipService;

    @Override
    public BasicObject createInventoryObjects(ObjectTypeDescription otd) {
        if (ObjectType.ITEM.equals(otd.getType())) {
            BasicObject basicObject = BasicObject.builder()
                    .objectTypeDescription(otd)
                    .durability(otd.getDurability())
                    .status(ObjectStatus.NOT_DEFINED)
                    .build();
            return basicObjectService.save(basicObject);
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Can't create Item. %s type is not 'ITEM'",ObjectTypeDescription.class.getSimpleName())
            );
        }
    }

    @Override
    public List<BasicObject> createInventoryObjects(ObjectTypeDescription otd, int quantity) {
        List<BasicObject> objects = new ArrayList<>();
        for(int i = 0; i < quantity; i++) {
            objects.add(createInventoryObjects(otd));
        }
        return objects;
    }

    @Override
    public StarShip createStarship(ObjectTypeDescription otd) {
        if (ObjectType.SHIP.equals(otd.getType())) {
            StarShip ship = StarShip.builder()
                    .objectTypeDescription(otd)
                    .durability(otd.getDurability())
                    .status(ObjectStatus.NOT_DEFINED)
                    .build();
            return starShipService.save(ship);
        }else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Can't create StarShip. %s type is not 'SHIP'",ObjectTypeDescription.class.getSimpleName())
            );
        }
    }
}
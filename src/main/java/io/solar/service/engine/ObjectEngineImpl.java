package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ObjectEngineImpl implements ObjectEngine {

    private final BasicObjectService basicObjectService;

    @Override
    public BasicObject createInventoryObject(ObjectTypeDescription otd) {
        BasicObject basicObject = BasicObject.builder()
                .objectTypeDescription(otd)
                .durability((long) otd.getDurability())
                .status(ObjectStatus.NOT_DEFINED)
                .build();

        return basicObjectService.save(basicObject);
    }

    @Override
    public List<BasicObject> createInventoryObject(ObjectTypeDescription otd, int quantity) {
        List<BasicObject> objects = new ArrayList<>();
        for(int i = 0; i < quantity; i++) {
            objects.add(createInventoryObject(otd));
        }
        return objects;
    }


}

package io.solar.service.engine;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryEngineImpl implements InventoryEngine {

    private final BasicObjectRepository basicObjectRepository;
    private final SpaceTechEngine spaceTechEngine;

    @Override
    public int putToInventory(SpaceTech location, List<BasicObject> items) {
        if (spaceTechEngine.isThereEnoughSpaceForObjects(location, items)) {
            items.forEach(s -> {
                s.setAttachedToShip((BasicObject) location);
                s.setX(null);
                s.setY(null);
                s.setStatus(ObjectStatus.ATTACHED_TO);
            });
            basicObjectRepository.saveAll(items);
            return items.size();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "There is no enough space in SpaceTech container to put items in it");
        }
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, BasicObject object) {
        return ship.getId().equals(object.getAttachedToShip().getId());
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, List<BasicObject> objects) {
        return objects.stream().allMatch(obj -> isInShipInventory(ship, obj));
    }

    @Override
    public void moveToMarketplace(BasicObject object) {
        object.setAttachedToShip(null);
        object.setStatus(ObjectStatus.AT_MARKETPLACE);
        basicObjectRepository.save(object);
    }
}
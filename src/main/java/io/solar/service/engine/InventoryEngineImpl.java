package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectStatus;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.engine.interfaces.InventoryEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryEngineImpl implements InventoryEngine {

    private final BasicObjectRepository basicObjectRepository;

    @Override
    public int putToInventory(BasicObject location, List<BasicObject> items) {
        items.forEach(s -> {
            s.setAttachedToShip(location);
            s.setStatus(ObjectStatus.ATTACHED_TO);
        });
        basicObjectRepository.saveAll(items);
        return items.size();
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, BasicObject object) {
        return ship.getId().equals(object.getAttachedToShip().getId());
    }

    @Override
    public boolean isInShipInventory(BasicObject ship, List<BasicObject> objects) {
        return objects.stream().allMatch(obj -> isInShipInventory(ship, obj));
    }
}

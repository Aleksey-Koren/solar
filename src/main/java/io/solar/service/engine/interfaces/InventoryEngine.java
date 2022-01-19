package io.solar.service.engine.interfaces;

import io.solar.entity.objects.BasicObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface InventoryEngine {

    int putToInventory(BasicObject location, List<BasicObject> items);
}

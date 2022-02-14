package io.solar.service.engine.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.service.engine.interfaces.inventory.socket.EnergyEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnergyEngineImpl implements EnergyEngine {


    @Override
    public void recalculateEnergy(SpaceTech spaceTech) {

    }
}

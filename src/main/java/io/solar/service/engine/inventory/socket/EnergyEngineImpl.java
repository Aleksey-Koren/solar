package io.solar.service.engine.inventory.socket;

import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.engine.interfaces.inventory.socket.EnergyEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EnergyEngineImpl implements EnergyEngine {

    private final SpaceTechEngine spaceTechEngine;
    private final BasicObjectService basicObjectService;

    @Override
    public void recalculateEnergy(SpaceTech spaceTech) {
        double required = spaceTechEngine.calculateRequiredAmountOfEnergy(spaceTech);
        double current = spaceTechEngine.calculateCurrentEnergyAmount(spaceTech);

        if (current < required) {
            disableAccordingPriorities(spaceTech, (long) required, (long) current);
        }
    }

    private void disableAccordingPriorities(SpaceTech spaceTech, long required, long current) {
        List<InventoryType> energyTypes = spaceTechEngine.retrieveEnergyTypes();
        List<BasicObject> notGeneratorItems = new ArrayList<>();

        List<SpaceTechSocket> sortedAndEnabled = spaceTech.getSockets().stream()
                .sorted(Comparator.comparingInt(SpaceTechSocket::getEnergyConsumptionPriority))
                .peek(s -> {
                    if (s.getObject() != null && !energyTypes.contains(s.getObject().getObjectTypeDescription().getInventoryType())) {
                        s.getObject().setIsEnabled(true);
                        notGeneratorItems.add(s.getObject());
                    }
                })
                .toList();

        List<SpaceTechSocket> disabled = new ArrayList<>();

        Collections.reverse(sortedAndEnabled);
        for(SpaceTechSocket socket : sortedAndEnabled) {
            if(socket.getObject() != null && !energyTypes.contains(socket.getObject().getObjectTypeDescription().getInventoryType())) {
                socket.getObject().setIsEnabled(false);
                required -= socket.getObject().getEnergyConsumption();
                disabled.add(socket);
                if (required <= current) {
                    break;
                }
            }
        }

        long restOfEnergy = current - required;

        if(restOfEnergy == 0) {
            return;
        }

        Collections.reverse(disabled);

        for (SpaceTechSocket socket : disabled) {
            if (restOfEnergy >= socket.getObject().getEnergyConsumption()) {
                socket.getObject().setIsEnabled(true);
                restOfEnergy -= socket.getObject().getEnergyConsumption();
            }
        }

        basicObjectService.saveAll(notGeneratorItems);
    }
}
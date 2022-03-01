package io.solar.service.engine.modification;

import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ParameterModification;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.service.engine.interfaces.modification.ModificationEngine;
import io.solar.service.modification.ModificationPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ModificationEngineImpl implements ModificationEngine {

    private final ModificationPriceService modificationPriceService;

    @Override
    public void applyModification(BasicObject item, Modification modification) {
        List<ParameterModification> parameterModifications = modification.getParameterModifications();
        item.setModification(modification);
        parameterModifications.forEach(s -> modifyParameter(s, item));
    }

    private void modifyParameter(ParameterModification parameterModification, BasicObject item) {
        switch (parameterModification.getParameterType()) {
            case VOLUME:
                item.setVolume((float)(item.getVolume() * parameterModification.getModificationValue()));
        }
    }

    @Override
    public boolean isStationAbleToModify(Modification modification, Station station) {
        return modificationPriceService.findByStationAndModification(station, modification).isPresent();
    }

    @Override
    public boolean isPossibleToApplyToItem(Modification modification, BasicObject item) {
        return modification.getAvailableObjectTypeDescriptions().contains(item.getObjectTypeDescription());
    }
}
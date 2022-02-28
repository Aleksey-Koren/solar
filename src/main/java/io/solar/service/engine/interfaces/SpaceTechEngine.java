package io.solar.service.engine.interfaces;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;

import java.util.List;

public interface SpaceTechEngine {

    Double retrieveViewDistance(SpaceTech spaceTech);

    Integer calculateMass(SpaceTech spaceTech);

    float calculateMaxThrust(SpaceTech spaceTech);

    float calculateMaxAcceleration(SpaceTech spaceTech);

    float calculateTotalVolume(SpaceTech spaceTech);

    float calculateUsedVolume(SpaceTech spaceTech);

    float calculateFreeAvailableVolume(SpaceTech spaceTech);

    long calculateGeneralEnergyAmount(SpaceTech spaceTech);

    long calculateAmountOfEnergyUsed(SpaceTech spaceTech);

    boolean isThereEnoughSpaceForObjects(SpaceTech ship, List<BasicObject> objects);

    boolean isThereEnoughSpaceForGoods(SpaceTech spaceTech, List<Goods> goods);

    boolean isUserOwnsThisSpaceTech(User user, SpaceTech spaceTech);

    boolean isUserAtStation(User user, Station station);
}
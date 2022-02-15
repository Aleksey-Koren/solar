package io.solar.service.engine.interfaces;

import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;

import java.util.List;

public interface SpaceTechEngine {

    Double retrieveViewDistance(SpaceTech spaceTech);

    Integer calculateMass(SpaceTech spaceTech);

    float calculateMaxThrust(SpaceTech spaceTech);

    float calculateMaxAcceleration(SpaceTech spaceTech);

    float calculateSpaceTechVolume(SpaceTech spaceTech);

    double calculateCurrentEnergyAmount(SpaceTech spaceTech);

    public double calculateRequiredAmountOfEnergy(SpaceTech spaceTech);

    boolean isThereEnoughSpaceForObjects(SpaceTech ship, List<BasicObject> objects);

    //todo I suppose it doesn't needed
    boolean isThereEnoughEnergyForObject(SpaceTech ship, BasicObject object);

    boolean isUserOwnsThisSpaceTech(User user, SpaceTech spaceTech);

    public List<InventoryType> retrieveEnergyTypes();
}
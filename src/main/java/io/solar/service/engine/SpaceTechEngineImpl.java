package io.solar.service.engine;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.InventoryType;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.inventory.InventoryTypeService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;

@Component
@RequiredArgsConstructor
public class SpaceTechEngineImpl implements SpaceTechEngine {

    @Value("${app.object.types.container}")
    private String containerObjectTypeTitle;
    @Value("${app.object.types.radar}")
    private String radarObjectTypeTitle;
    @Value("${app.object.types.engine}")
    private String engineObjectTypeTitle;
    @Value("${app.object.types.huge_engine}")
    private String hugeEngineObjectTypeTitle;

    private final BasicObjectRepository basicObjectRepository;
    private final InventoryTypeService inventoryTypeService;
    private final BasicObjectService basicObjectService;
    private final StationService stationService;
    private final UserService userService;

    @Override
    public Double retrieveViewDistance(SpaceTech spaceTech) {
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        InventoryType radar = inventoryTypeService.getByTitle(radarObjectTypeTitle);

        List<BasicObject> radars = basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), radar);

        return radars.stream()
                .map(s -> s.getObjectTypeDescription().getDistance()).mapToDouble(Float::doubleValue)
                .distinct()
                .max()
                .orElse(0);
    }

    @Override
    public Integer calculateMass(SpaceTech spaceTech) {
        BasicObject spaceTechObject = (BasicObject) spaceTech;

        int starshipMass = spaceTechObject.getObjectTypeDescription().getMass();

        int attachedObjectsMass = spaceTechObject.getAttachedObjects()
                .stream()
                .mapToInt(object -> object.getObjectTypeDescription().getMass())
                .sum();

//        return starshipMass + attachedObjectsMass;
        return 100;
    }

    @Override
    public float calculateMaxThrust(SpaceTech spaceTech) {
        BasicObject spaceTechAsObject = (BasicObject) spaceTech;
        InventoryType engine = inventoryTypeService.getByTitle(engineObjectTypeTitle);
        InventoryType hugeEngine = inventoryTypeService.getByTitle(hugeEngineObjectTypeTitle);

        List<BasicObject> engines = basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), engine);

        engines.addAll(basicObjectRepository.getObjectsInSlotsByType(spaceTechAsObject.getId(), hugeEngine));

        return (float) engines.stream()
                .filter(BasicObject::getIsEnabled)
                .mapToDouble(s -> (double) s.getObjectTypeDescription().getPowerMax())
                .sum();
    }

    @Override
    public float calculateTotalVolume(SpaceTech spaceTech) {
        BasicObject ship = (BasicObject) spaceTech;

        InventoryType container = inventoryTypeService.getByTitle(containerObjectTypeTitle);
        List<BasicObject> containers = basicObjectService.getObjectsInSlotsByType(ship.getId(), container);

        return (float) containers.stream()
                .mapToDouble(BasicObject::getVolume)
                .sum();
    }

    @Override
    public float calculateUsedVolume(SpaceTech spaceTech) {
        double objectsVolume = spaceTech.getAttachedObjects()
                .stream()
                .filter(not(inventoryTypeService::isContainer))
                .mapToDouble(BasicObject::getVolume)
                .sum();

        double goodsVolume = spaceTech.getGoods()
                .stream()
                .mapToDouble(goods -> goods.getProduct().getVolume() * goods.getAmount())
                .sum();

        return (float) (objectsVolume + goodsVolume);
    }

    @Override
    public float calculateFreeAvailableVolume(SpaceTech spaceTech) {
        return calculateTotalVolume(spaceTech) - calculateUsedVolume(spaceTech);
    }

    @Override
    public boolean isThereEnoughSpaceForObjects(SpaceTech spaceTech, List<BasicObject> objects) {
        float shipVolume = calculateTotalVolume(spaceTech);

        float objectsVolume = (float) objects.stream()
                .mapToDouble(BasicObject::getVolume)
                .sum();

        float usedVolume = calculateUsedVolume(spaceTech);

        return ((usedVolume + objectsVolume) <= shipVolume);
    }

    @Override
    public boolean isThereEnoughSpaceForGoods(SpaceTech spaceTech, List<Goods> goods) {
        double goodsVolume = goods.stream()
                .mapToDouble(goodProduct -> goodProduct.getProduct().getBulk() * goodProduct.getAmount())
                .sum();

        return goodsVolume <= calculateFreeAvailableVolume(spaceTech);
    }

    /**
     * Object.energyConsumption for objects of generator types is amount of energy generator produces
     */
    @Override
    public long calculateGeneralEnergyAmount(SpaceTech spaceTech) {
        return spaceTech.getSockets().stream()
                .filter(s -> s.getObject() != null && inventoryTypeService.isGenerator(s.getObject()))
                .mapToLong(socket -> socket.getObject().getEnergyConsumption())
                .sum();
    }

    @Override
    public long calculateAmountOfEnergyUsed(SpaceTech spaceTech) {

        return spaceTech.getSockets().stream()
                .filter(s -> (s.getObject() != null && !inventoryTypeService.isGenerator(s.getObject())))
                .mapToLong(socket -> socket.getObject().getEnergyConsumption())
                .sum();
    }

    @Override
    public float calculateMaxAcceleration(SpaceTech spaceTech) {
        return calculateMaxThrust(spaceTech) / calculateMass(spaceTech);
    }

    @Override
    public boolean isUserOwnsThisSpaceTech(User user, SpaceTech spaceTech) {
        return user.equals(spaceTech.getUser());
    }

    @Override
    public boolean isUserAtStation(User user, Station station) {
        return station.equals(user.getLocation().getAttachedToShip());
    }

    @Override
    public boolean isUserAtStation(User user) {
        if (user.getLocation().getAttachedToShip() == null) {
            return false;
        }
        Optional<Station> stationOpt = stationService.findById(user.getLocation().getAttachedToShip().getId());
        return stationOpt.isPresent();
    }


}
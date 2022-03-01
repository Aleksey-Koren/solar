package io.solar.entity.proxy;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ParameterModification;
import io.solar.entity.modification.ParameterType;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

public class BasicObjectProxy extends BasicObject {

    @Getter
    private BasicObject object;

    public BasicObjectProxy(BasicObject object) {
        this.object = object;
        this.id = object.getId();
        this.population = object.getPopulation();
        this.fraction = object.getFraction();
        this.title = object.getTitle();
        this.x = object.getX();
        this.y = object.getY();
        this.aphelion = object.getAphelion();
        this.orbitalPeriod = object.getOrbitalPeriod();
        this.angle = object.getAngle();
        this.rotationAngle = object.getRotationAngle();
        this.active = object.getActive();
        this.durability = object.getDurability();
        this.status = object.getStatus();
        this.speedX = object.getSpeedX();
        this.speedY = object.getSpeedY();
        this.accelerationX = object.getAccelerationX();
        this.accelerationY = object.getAccelerationY();
        this.positionIteration = object.getPositionIteration();
        this.positionIterationTs = object.getPositionIterationTs();
        this.clockwiseRotation = object.getClockwiseRotation();
        this.volume = object.getVolume();
        this.energyConsumption = object.getEnergyConsumption();
        this.isEnabled = object.getIsEnabled();
    }

    @Override
    public Planet getPlanet() {
        return object.getPlanet();
    }

    @Override
    public ObjectTypeDescription getObjectTypeDescription() {
        return object.getObjectTypeDescription();
    }

    @Override
    public BasicObject getAttachedToShip() {
        return object.getAttachedToShip();
    }

    @Override
    public InventorySocket getAttachedToSocket() {
        return object.getAttachedToSocket();
    }

    @Override
    public Modification getModification() {
        return object.getModification();
    }

    @Override
    public List<BasicObject> getAttachedObjects() {
        return object.getAttachedObjects();
    }

    @Override
    public List<SpaceTechSocket> getSockets() {
        return object.getSockets();
    }

    @Override
    public List<Course> getCourses() {
        return object.getCourses();
    }

    public int getMaxDurability() {
        Optional<ParameterModification> paramOpt = getParameterModification(object, ParameterType.DURABILITY);
        int basicDurability = object.getObjectTypeDescription().getDurability();
        return paramOpt
                .map(parameterModification -> (int) (basicDurability * parameterModification.getModificationValue()))
                .orElse(basicDurability);
    }

    private Optional<ParameterModification> getParameterModification(BasicObject object, ParameterType type) {
        if(object.getModification() == null) {
            return Optional.empty();
        }

        return object.getModification().getParameterModifications().stream()
                .filter(s -> s.getParameterType().equals(type)).findFirst();
    }
}
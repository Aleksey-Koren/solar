package io.solar.service.scheduler;

import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.PlanetService;
import io.solar.service.UtilityService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ObjectCoordinatesService {
    private final String POSITION_ITERATION_UTILITY_KEY = "position_iteration";

    @Value("${app.navigator.num_update_object}")
    private Integer amountReceivedObjects;

    private final UtilityService utilityService;
    private final BasicObjectRepository basicObjectRepository;
    private final PlanetService planetService;

    private final Map<Long, Planet> PLANETS = new HashMap<>();

    @Transactional
    public void update() {
        String positionIteration = utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1");
        long currentIteration = Long.parseLong(positionIteration);
        List<BasicObject> objects;

        updatePlanets(planetService.findAll());

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {

            updateObjects(objects, currentIteration);
            basicObjectRepository.saveAllAndFlush(objects);
        }

        utilityService.updateValueByKey(POSITION_ITERATION_UTILITY_KEY, String.valueOf(currentIteration + 1));
        PLANETS.clear();
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
    }

    //TODO: need to refactor the code
    private void updatePlanets(List<Planet> planets) {
        Planet sun = planetService.findSun();
        List<Planet> moons = new ArrayList<>();
        long now = System.currentTimeMillis();

        planets.stream()
                .filter(planet -> planet.getParent() != null)
                .forEach(planet -> {
                    if (planet.getParent().equals(sun.getId())) {
                        updateOrbitalObject(planet, now);
                        PLANETS.put(planet.getId(), planet);
                    } else {
                        moons.add(planet);
                    }
                });

        moons.forEach(moon -> updateOrbitalObject(moon, now));
        moons.addAll(PLANETS.values());

        planetService.saveAll(moons);
    }

    private void updateObjects(List<BasicObject> objects, long currentIteration) {
        long now = System.currentTimeMillis();

        objects.forEach(object -> {
            if (object.getPlanet() != null && object.getAphelion() != null
                    && object.getAngle() != null && object.getOrbitalPeriod() != null) {
                updateOrbitalObject(object, now);
            } else {
                updateUnattachedObject(object, now, currentIteration);
            }
        });
    }

    private void updateOrbitalObject(BasicObject object, Long now) {
        Double delta = calculateDelta(now);
        double da = delta / object.getOrbitalPeriod();
        object.setAngle(object.getAngle() + (float) da);

//        Planet parentPlanet = PLANETS.get(object.getId()).getParent();
//        object.setX(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), parentPlanet.getX()));
//        object.setY(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), parentPlanet.getY()));
    }

    private void updateUnattachedObject(BasicObject object, Long currentTimeMills, Long currentIteration) {
        long time = currentTimeMills - object.getPositionIterationTs();
        object.setX(determinePosition(object.getX(), object.getSpeedX(), time));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), time));

        object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getAccelerationX(), time));
        object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getAccelerationY(), time));

        object.setPositionIterationTs(currentTimeMills);
        object.setPositionIteration(currentIteration + 1);
    }

    private Double calculateDelta(Long currentTimeMills) {
        Instant epoch = LocalDateTime.of(2019, 11, 12, 0, 0, 0, 0)
                .toInstant(ZoneOffset.UTC);

        return Math.PI * 2 * (currentTimeMills - epoch.toEpochMilli()) / (1000 * 60 * 60 * 24);
    }

    private Float calculateAbsoluteCoordinate(Float angle, Float aphelion, Float parentPosition) {

        return (float) Math.cos(angle) * aphelion + parentPosition;
    }

    private Float determinePosition(Float coordinate, Float speed, Long time) {

        return coordinate + (speed * time / 3_600_000);
    }

    private Float calculateSpeed(Float speed, Float acceleration, long time) {

        return speed + (acceleration * time / 3_600_000);
    }
}
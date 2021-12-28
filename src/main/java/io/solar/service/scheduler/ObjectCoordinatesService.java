package io.solar.service.scheduler;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.CourseService;
import io.solar.service.PlanetService;
import io.solar.service.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectCoordinatesService {
    private final String POSITION_ITERATION_UTILITY_KEY = "position_iteration";
    private final String SCHEDULER_TIME_UTILITY_KEY = "scheduler_time";

    @Value("${app.navigator.num_update_object}")
    private Integer amountReceivedObjects;

    @Value("${app.navigator.update_coordinates_delay}")
    private String schedulerDelaySeconds;

    private final UtilityService utilityService;
    private final BasicObjectRepository basicObjectRepository;
    private final PlanetService planetService;
    private final CourseService courseService;

    @Transactional
    public void update() {
        long currentIteration = Long.parseLong(utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1"));
        long schedulerTime = Long.parseLong(utilityService.getValue(SCHEDULER_TIME_UTILITY_KEY, "0"));
        long now = System.currentTimeMillis();

        List<BasicObject> objects;

        updatePlanets(now);

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {
            updateObjects(objects, currentIteration, now, schedulerTime);
            basicObjectRepository.saveAllAndFlush(objects);
            utilityService.updateValueByKey(SCHEDULER_TIME_UTILITY_KEY, String.valueOf(System.currentTimeMillis()));
        }

        utilityService.updateValueByKey(POSITION_ITERATION_UTILITY_KEY, String.valueOf(currentIteration + 1));
    }

    private void updatePlanets(long now) {
        List<Planet> planets = planetService.findAll();
        List<Planet> moons = new ArrayList<>();
        Planet sun = planetService.findSun();

        planets.stream()
                .filter(planet -> planet.getPlanet() != null)
                .forEach(planet -> {
                    if (sun.equals(planet.getPlanet())) {
                        updateOrbitalObject(planet, now);
                    } else {
                        moons.add(planet);
                    }
                });

        moons.forEach(moon -> updateOrbitalObject(moon, now));

        planetService.saveAll(planets);
    }


    //todo: if statement need refactor -> maybe add field 'isOnOrbit'
    private void updateObjects(List<BasicObject> objects, long currentIteration, long now, long previousSchedulerTime) {
        objects.forEach(object -> {
            if (object.getPlanet() != null && object.getAphelion() != null
                    && object.getAngle() != null && object.getOrbitalPeriod() != null) {

                updateOrbitalObject(object, now);
            } else {

                updateUnattachedObject(object, now, currentIteration, previousSchedulerTime);
            }
        });
    }

    private void updateOrbitalObject(BasicObject object, Long now) {
        double da = calculateDelta(now) / object.getOrbitalPeriod();
        object.setAngle(object.getAngle() + (float) da);

        object.setX(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getX()));
        object.setY(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getY()));
    }

    private void updateUnattachedObject(BasicObject object, Long currentTimeMills, Long currentIteration, Long previousSchedulerTime) {

        if (calculateAcceleration(object.getAccelerationX(), object.getAccelerationY()) > object.getMaxAcceleration()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Starship with id = %d acceleration > maxAcceleration", object.getId()));
        }

        long time = currentTimeMills - object.getPositionIterationTs();

//        Float speedX = object.getSpeedX();
//        Float speedY = object.getSpeedY();
//        Float x = object.getX();
//        Float y = object.getY();
//
//        Course activeCourse = courseService.findActiveCourse(object, Instant.ofEpochMilli(previousSchedulerTime));
//        Instant delta = Instant.ofEpochMilli(currentTimeMills - previousSchedulerTime);
//        long schedulerInterval = Duration.parse(schedulerDelaySeconds).toMillis();
//
//        if (activeCourse != null) {
//            long currentTime = previousSchedulerTime;
//            while (activeCourse != null && activeCourse.getExpireAt().toEpochMilli() <= delta.toEpochMilli() + schedulerInterval + currentTimeMills) {
//                long executionTime = object.getPositionIteration() - activeCourse.getExpireAt().toEpochMilli();
//
//                Float updatedX = determinePosition(x, speedX, executionTime);
//                Float updatedY = determinePosition(y, speedY, executionTime);
//
//                Float updatedSpeedX = calculateSpeed(speedX, activeCourse.getAccelerationX(), executionTime);
//                Float updatedSpeedY = calculateSpeed(speedY, activeCourse.getAccelerationY(), executionTime);
//
//                activeCourse = activeCourse.getNext();
//            }
//        }

        object.setX(determinePosition(object.getX(), object.getSpeedX(), time));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), time));

        object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getAccelerationX(), time));
        object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getAccelerationY(), time));

        object.setPositionIterationTs(currentTimeMills);
        object.setPositionIteration(currentIteration + 1);
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
    }

    private Double calculateAcceleration(Float accelerationX, Float accelerationY) {

        return Math.sqrt(Math.pow(accelerationX, 2) + Math.pow(accelerationY, 2));
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
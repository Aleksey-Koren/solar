package io.solar.service.scheduler;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.CourseService;
import io.solar.service.PlanetService;
import io.solar.service.UtilityService;
import io.solar.service.object.BasicObjectService;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ObjectCoordinatesService {
    private final String POSITION_ITERATION_UTILITY_KEY = "position_iteration";

    @Value("${app.navigator.num_update_object}")
    private Integer amountReceivedObjects;

    private final UtilityService utilityService;
    private final BasicObjectRepository basicObjectRepository;
    private final PlanetService planetService;
    private final CourseService courseService;

    @Transactional
    public void update() {
        String positionIteration = utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1");
        long currentIteration = Long.parseLong(positionIteration);
        long now = System.currentTimeMillis();
        List<BasicObject> objects;

        updatePlanets(now);

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {

            updateObjects(objects, currentIteration, now);
            basicObjectRepository.saveAllAndFlush(objects);
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
    private void updateObjects(List<BasicObject> objects, long currentIteration, long now) {
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
        double da = calculateDelta(now) / object.getOrbitalPeriod();
        object.setAngle(object.getAngle() + (float) da);

        object.setX(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getX()));
        object.setY(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getY()));
    }

    private void updateUnattachedObject(BasicObject object, Long currentTimeMills, Long currentIteration) {
        long time = currentTimeMills - object.getPositionIterationTs();
//        Float speedX = object.getSpeedX();
//        Float speedY = object.getSpeedY();
//        Float x = object.getX();
//        Float y = object.getY();
//        Course lastCompletedCourse = courseService.findLastCompletedCourse(object);
//
//        if (object.getPositionIteration() > lastCompletedCourse.getExpireAt().toEpochMilli()) {
//            Course actualCourse = lastCompletedCourse.getNext();
//            if (actualCourse != null) {
//                long executionTime = object.getPositionIteration() - lastCompletedCourse.getExpireAt().toEpochMilli();
//
//                Float updatedX = determinePosition(x, speedX, executionTime);
//                Float updatedY = determinePosition(y, speedY, executionTime);
//
//                Float updatedSpeedX = calculateSpeed(speedX, actualCourse.getAccelerationX(), executionTime);
//                Float updatedSpeedY = calculateSpeed(speedY, actualCourse.getAccelerationY(), executionTime);
//
//                object.setX(updatedX);
//                object.setY(updatedY);
//                object.setSpeedX(updatedSpeedX);
//                object.setSpeedY(updatedSpeedY);
//                object.setPositionIteration(currentTimeMills);
//                object.setPositionIterationTs(currentIteration + 1);
//            }
//        } else {
//            Course course = findCourse(lastCompletedCourse, object.getPositionIteration());
//            if (course != null) {
//                Float updatedX = x;
//                Float updatedY = y;
//                Float updatedSpeedX = speedX;
//                Float updatedSpeedY = speedY;
//                while (course != null) {
//                    Course previousCourse = courseService.findByNext(course).get();
//                    updatedX = determinePosition(updatedX, speedX, null);
//                    updatedY = determinePosition(updatedY, speedY, null);
//
//                    updatedSpeedX = calculateSpeed(updatedSpeedX, course.getAccelerationX(), course.getExpireAt().toEpochMilli() - object.getPositionIteration());
//                    updatedSpeedY = calculateSpeed(updatedSpeedY, course.getAccelerationY(), course.getExpireAt().toEpochMilli() - object.getPositionIteration());
//
//                    course = course.getNext();
//                }
//            }
//        }
        object.setX(determinePosition(object.getX(), object.getSpeedX(), time));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), time));

        object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getAccelerationX(), time));
        object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getAccelerationY(), time));

        object.setPositionIterationTs(currentTimeMills);
        object.setPositionIteration(currentIteration + 1);
    }

    private Course findCourse(Course course, Long positionIteration) {

        if (course == null || course.getExpireAt().toEpochMilli() < positionIteration) {
            return course;
        }

        return findCourse(courseService.findByNext(course).get(), positionIteration);
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
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
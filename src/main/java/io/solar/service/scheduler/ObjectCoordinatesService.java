package io.solar.service.scheduler;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.CourseService;
import io.solar.service.PlanetService;
import io.solar.service.UtilityService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final SpaceTechEngine spaceTechEngine;

    @Transactional
    public void update() {
        long now = System.currentTimeMillis();
        long schedulerDuration = Duration.parse(schedulerDelaySeconds).toMillis();
        long currentIteration = Long.parseLong(utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1"));
        List<BasicObject> objects;

        updatePlanets(now);

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {
            updateObjects(objects, currentIteration, now, schedulerDuration);
            basicObjectRepository.saveAllAndFlush(objects);
        }

        utilityService.updateValueByKey(POSITION_ITERATION_UTILITY_KEY, String.valueOf(currentIteration + 1));
        //todo: save scheduler time
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

    private void updateObjects(List<BasicObject> objects, long currentIteration, long now, long schedulerDuration) {
        objects.forEach(object -> {
            if (object.getPlanet() != null && object.getAphelion() != null
                    && object.getAngle() != null && object.getOrbitalPeriod() != null) {

                updateOrbitalObject(object, now);
            } else {

                updateUnattachedObject(object, now, schedulerDuration);
                object.setPositionIterationTs(now);
                object.setPositionIteration(currentIteration + 1);
            }
        });
    }

    private void updateOrbitalObject(BasicObject object, Long now) {
        double da = calculateDelta(now) / object.getOrbitalPeriod();
        object.setAngle(object.getAngle() + (float) da);

        object.setX(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getX()));
        object.setY(calculateAbsoluteCoordinate(object.getAngle(), object.getAphelion(), object.getPlanet().getY()));
    }

    private void updateUnattachedObject(BasicObject object, Long currentTimeMills, Long schedulerDuration) {

        Course activeCourse = courseService.findActiveCourse(object, Instant.ofEpochMilli(currentTimeMills));

        activeCourse = completeObjectCourses(activeCourse, object, currentTimeMills, schedulerDuration);

        if (activeCourse == null && object.getSpeed() > 0) {
            staticObjectMotion(object, currentTimeMills, schedulerDuration);
        }

    }

    private void staticObjectMotion(BasicObject object, Long currentTimeMillis, Long schedulerDuration) {
        Course lastCourse = courseService.findLastCourse(object).orElseThrow();

        // is before or equals
        long motionDuration = lastCourse.getExpireAt().compareTo(Instant.ofEpochMilli(currentTimeMillis)) <= 0
                ? schedulerDuration
                : lastCourse.getExpireAt().toEpochMilli() - currentTimeMillis;

        object.setX(determinePosition(object.getX(), object.getSpeedX(), motionDuration, 0f));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), motionDuration, 0f));
    }

    private Course completeObjectCourses(Course activeCourse, BasicObject object, Long currentTimeMillis, Long schedulerDuration) {
        long courseDuration = 0;
        while (activeCourse != null) {

            if (isAccelerationInvalid(object, activeCourse)) {
                throw new ServiceException(
                        String.format("Starship/Station with id = %d acceleration > maxAcceleration", object.getId())
                );
            }

            courseDuration = calculateCourseDuration(activeCourse, schedulerDuration, Instant.ofEpochMilli(currentTimeMillis));


            object.setX(determinePosition(object.getX(), object.getSpeedX(), courseDuration, activeCourse.getAccelerationX()));
            object.setY(determinePosition(object.getY(), object.getSpeedY(), courseDuration, activeCourse.getAccelerationY()));

            object.setSpeedX(calculateSpeed(object.getSpeedX(), activeCourse.getAccelerationX(), courseDuration));
            object.setSpeedY(calculateSpeed(object.getSpeedY(), activeCourse.getAccelerationY(), courseDuration));

            object.setAccelerationX(activeCourse.getAccelerationX());
            object.setAccelerationY(activeCourse.getAccelerationY());

            if (activeCourse.hasNext() && activeCourse.getExpireAt().isBefore(Instant.ofEpochMilli(currentTimeMillis + schedulerDuration))) {
                activeCourse.getNext().setExpireAt(activeCourse.getExpireAt().plusMillis(activeCourse.getNext().getTime()));
                activeCourse = activeCourse.getNext();
            } else {
                if (activeCourse.getExpireAt().isBefore(Instant.ofEpochMilli(currentTimeMillis + schedulerDuration))) {
                    staticObjectMotion();
                }
                    activeCourse = null;
            }

        }

        return activeCourse;


//        long courseDuration = 0;
//        while (activeCourse != null && courseDuration < schedulerDuration) {
//
//            if (isAccelerationInvalid(object, activeCourse)) {
//                throw new ServiceException(
//                        String.format("Starship/Station with id = %d acceleration > maxAcceleration", object.getId())
//                );
//            }
//
//            courseDuration = calculateCourseDuration(activeCourse, schedulerDuration, Instant.ofEpochMilli(currentTimeMillis));
//
//            object.setX(determinePosition(object.getX(), object.getSpeedX(), courseDuration, activeCourse.getAccelerationX()));
//            object.setY(determinePosition(object.getY(), object.getSpeedY(), courseDuration, activeCourse.getAccelerationY()));
//
//            object.setSpeedX(calculateSpeed(object.getSpeedX(), activeCourse.getAccelerationX(), courseDuration));
//            object.setSpeedY(calculateSpeed(object.getSpeedY(), activeCourse.getAccelerationY(), courseDuration));
//
//            object.setAccelerationX(activeCourse.getAccelerationX());
//            object.setAccelerationY(activeCourse.getAccelerationY());
//
//            activeCourse.setExpireAt(null);
//
//            activeCourse = activeCourse.getNext();
//        }
//
//        return activeCourse;
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
    }

    private Long calculateCourseDuration(Course activeCourse, Long schedulerInterval, Instant currentTime) {
        Instant endSchedulerInstant = Instant.ofEpochMilli(currentTime.toEpochMilli() + schedulerInterval);
        long courseDuration;

        if (activeCourse.getPrevious() == null) {
            courseDuration = Duration.between(currentTime, activeCourse.getExpireAt()).toMillis();
        } else {

            long timeBetweenCourseAndEndScheduler = Duration.between(activeCourse.getPrevious().getExpireAt(), endSchedulerInstant).toMillis();

            if (timeBetweenCourseAndEndScheduler > activeCourse.getTime()) {
                courseDuration = activeCourse.getTime();
            } else {
                courseDuration = timeBetweenCourseAndEndScheduler;
            }
        }

        return courseDuration;
    }

    private Double calculateDelta(Long currentTimeMills) {
        Instant epoch = LocalDateTime.of(2019, 11, 12, 0, 0, 0, 0)
                .toInstant(ZoneOffset.UTC);

        return Math.PI * 2 * (currentTimeMills - epoch.toEpochMilli()) / (1000 * 60 * 60 * 24);
    }

    private Float determinePosition(Float coordinate, Float speed, Long time, Float acceleration) {
        double dividedTime = time / 3_600_000d;
        float distanceCovered = (float) (speed * dividedTime + (acceleration * Math.pow(dividedTime, 2)) / 2);

        return coordinate + distanceCovered;
    }

    private boolean isAccelerationInvalid(BasicObject object, Course course) {
        Double courseAcceleration = calculateAcceleration(course.getAccelerationX(), course.getAccelerationY());

        return courseAcceleration > spaceTechEngine.calculateMaxAcceleration((SpaceTech) object);
    }

    private Double calculateAcceleration(Float accelerationX, Float accelerationY) {

        return Math.sqrt(Math.pow(accelerationX, 2) + Math.pow(accelerationY, 2));
    }

    private Float calculateAbsoluteCoordinate(Float angle, Float aphelion, Float parentPosition) {

        return (float) Math.cos(angle) * aphelion + parentPosition;
    }

    private Float calculateSpeed(Float speed, Float acceleration, long time) {

        return speed + (acceleration * time / 3_600_000);
    }
}
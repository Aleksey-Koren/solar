package io.solar.service.scheduler;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.CourseService;
import io.solar.service.NavigatorService;
import io.solar.service.PlanetService;
import io.solar.service.UtilityService;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NavigatorService navigatorService;

    @Transactional
    public void update() {
        long now = System.currentTimeMillis();
        long schedulerDuration = Duration.parse(schedulerDelaySeconds).toMillis();
        long currentIteration = Long.parseLong(utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1"));
        double delta = calculateDelta(now);
        List<BasicObject> objects;

        updatePlanets(delta);

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {
            updateObjects(objects, currentIteration, now, schedulerDuration, delta);
            basicObjectRepository.saveAllAndFlush(objects);
        }

        courseService.deleteAllExpiredCourses(Instant.ofEpochMilli(now + schedulerDuration));
        utilityService.updateValueByKey(POSITION_ITERATION_UTILITY_KEY, String.valueOf(currentIteration + 1));
        utilityService.updateValueByKey(SCHEDULER_TIME_UTILITY_KEY, String.valueOf(System.currentTimeMillis() - now));
    }

    private void updatePlanets(Double delta) {
        List<Planet> planets = planetService.findAll();
        List<Planet> moons = new ArrayList<>();
        Planet sun = planetService.findSun();

        planets.stream()
                .filter(planet -> planet.getPlanet() != null)
                .forEach(planet -> {
                    if (sun.equals(planet.getPlanet())) {
                        updateOrbitalObject(planet, delta);
                    } else {
                        moons.add(planet);
                    }
                });

        moons.forEach(moon -> updateOrbitalObject(moon, delta));

        planetService.saveAll(planets);
    }

    private void updateObjects(List<BasicObject> objects, long currentIteration, long now, long schedulerDuration, double delta) {
        objects.forEach(object -> {
            if (object.getPlanet() != null && object.getAphelion() != null
                    && object.getAngle() != null && object.getOrbitalPeriod() != null) {

                updateOrbitalObject(object, delta);
            } else {

                updateUnattachedObject(object, now, schedulerDuration);
            }
            object.setPositionIterationTs(now);
            object.setPositionIteration(currentIteration + 1);
        });
    }

    private void updateOrbitalObject(BasicObject object, Double delta) {
        double deltaAngleRadians = delta / object.getOrbitalPeriod();

        Course activeCourse = courseService.findActiveCourse(object);
        if (activeCourse == null) {

        }else{
            navigatorService.leaveOrbit(object);
            courseService.deleteById(activeCourse.getId());
            updateUnattachedObject(object, currentTimeMills, schedulerDuration);
        }

        double da = delta / object.getOrbitalPeriod();

        double objectAngle = object.getClockwiseRotation()
                ? object.getAngle() - deltaAngleRadians
                : object.getAngle() + deltaAngleRadians;

        object.setX((float) Math.cos(objectAngle) * object.getAphelion() + object.getPlanet().getX());
        object.setY((float) Math.sin(objectAngle) * object.getAphelion() + object.getPlanet().getY());
        object.setAngle((float) objectAngle);
    }

    private void updateUnattachedObject(BasicObject object, Long currentTimeMills, Long schedulerDuration) {
        Course activeCourse = courseService.findActiveCourse(object);

        if (activeCourse == null) {
            staticObjectMotion(object, schedulerDuration);
        } else {

            completeObjectCourses(activeCourse, object, currentTimeMills, schedulerDuration);
        }
    }

    private void staticObjectMotion(BasicObject object, Long staticMotionLength) {
        object.setX(determinePosition(object.getX(), object.getSpeedX(), staticMotionLength, 0f));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), staticMotionLength, 0f));
    }

    private void completeObjectCourses(Course activeCourse, BasicObject object, Long schedulerStartTime, Long schedulerDuration) {
        Instant endSchedulerInstant = Instant.ofEpochMilli(schedulerStartTime + schedulerDuration);
        long courseDuration;

        while (true) {
            if (isAccelerationInvalid(object, activeCourse)) {
                throw new ServiceException(
                        String.format("Starship/Station with id = %d acceleration > maxAcceleration", object.getId())
                );
            }
            courseDuration = calculateCourseDuration(activeCourse, schedulerDuration, Instant.ofEpochMilli(schedulerStartTime));

            updateObjectFields(object, activeCourse, courseDuration);

            if (activeCourse.hasNext() && activeCourse.getExpireAt().isBefore(endSchedulerInstant)) {
                activeCourse = activeCourse.getNext();
                activeCourse.setExpireAt(activeCourse.getPrevious().getExpireAt().plusMillis(activeCourse.getTime()));

            } else {
                if (activeCourse.getExpireAt().isBefore(endSchedulerInstant)) {
                    long staticMotionDuration = Duration.between(activeCourse.getExpireAt(), endSchedulerInstant).toMillis();
                    staticObjectMotion(object, staticMotionDuration);
                }
                break;
            }
        }
    }

    private Long calculateCourseDuration(Course activeCourse, Long schedulerInterval, Instant schedulerStartTime) {
        Instant endSchedulerInstant = schedulerStartTime.plusMillis(schedulerInterval);
        long courseDuration;

        if (activeCourse.getPrevious() == null) {

            if (activeCourse.getExpireAt() == null) {
                courseDuration = activeCourse.getTime() > schedulerInterval
                        ? schedulerInterval
                        : activeCourse.getTime();

                activeCourse.setExpireAt(schedulerStartTime.plusMillis(activeCourse.getTime()));

            } else {
                courseDuration = Duration.between(schedulerStartTime, activeCourse.getExpireAt()).toMillis();
            }

        } else {
            long timeBetweenCourseAndEndScheduler = Duration.between(activeCourse.getPrevious().getExpireAt(), endSchedulerInstant).toMillis();

            courseDuration = timeBetweenCourseAndEndScheduler > activeCourse.getTime()
                    ? activeCourse.getTime()
                    : timeBetweenCourseAndEndScheduler;
        }

        return courseDuration;
    }

    private void updateObjectFields(BasicObject object, Course activeCourse, Long courseDuration) {
        object.setX(determinePosition(object.getX(), object.getSpeedX(), courseDuration, activeCourse.getAccelerationX()));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), courseDuration, activeCourse.getAccelerationY()));

        object.setSpeedX(calculateSpeed(object.getSpeedX(), activeCourse.getAccelerationX(), courseDuration));
        object.setSpeedY(calculateSpeed(object.getSpeedY(), activeCourse.getAccelerationY(), courseDuration));

        object.setAccelerationX(activeCourse.getAccelerationX());
        object.setAccelerationY(activeCourse.getAccelerationY());
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
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

    private Double calculateDelta(Long schedulerDuration) {

        return Math.PI * 2 * schedulerDuration / (1000 * 60 * 60 * 24);
    }

    private Double calculateAcceleration(Float accelerationX, Float accelerationY) {

        return Math.sqrt(Math.pow(accelerationX, 2) + Math.pow(accelerationY, 2));
    }

    private Float calculateSpeed(Float speed, Float acceleration, long time) {

        return speed + (acceleration * time / 3_600_000);
    }
}
package io.solar.service.scheduler;

import io.solar.config.properties.AppProperties;
import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.entity.objects.StarShip;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.CourseService;
import io.solar.service.NavigatorService;
import io.solar.service.PlanetService;
import io.solar.service.StarShipService;
import io.solar.service.UtilityService;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.engine.interfaces.StarShipEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
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
    private final StarShipService starShipService;
    private final CourseService courseService;
    private final StarShipEngine starShipEngine;
    private final NavigatorService navigatorService;
    private final NotificationEngine notificationEngine;
    private final AppProperties appProperties;

    @Transactional
    public void update(long now) {
        System.out.println("SCHEDULER START TIME: " + now);
        long schedulerDuration = Duration.parse(schedulerDelaySeconds).toMillis();
        long currentIteration = Long.parseLong(utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1"));
        double delta = calculateDelta(schedulerDuration);
        List<BasicObject> objects;

        updatePlanets(delta);

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {
            updateObjects(objects, now, schedulerDuration, delta);
            basicObjectRepository.saveAllAndFlush(objects);
        }

//        courseService.deleteAllExpiredCourses(now + schedulerDuration);
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
                        updateOrbitalObjectLocation(planet, delta);
                    } else {
                        moons.add(planet);
                    }
                });

        moons.forEach(moon -> updateOrbitalObjectLocation(moon, delta));

        planetService.saveAll(planets);
    }

    private void updateObjects(List<BasicObject> objects, long now, long schedulerDuration, double delta) {

        objects.forEach(object -> {
            if (object.getPlanet() != null && object.getAphelion() != null
                    && object.getAngle() != null && object.getOrbitalPeriod() != null) {

                updateOrbitalObject(object, delta, now, schedulerDuration);
            } else {

                updateUnattachedObject(object, now, schedulerDuration);
            }
            object.setPositionIterationTs(now);
            object.setPositionIteration(object.getPositionIteration() + 1);
        });
    }

    private void updateOrbitalObject(BasicObject object, Double delta, Long currentTimeMills, Long schedulerDuration) {
        Course activeCourse = courseService.findActiveCourse(object);
        if (activeCourse == null) {
            updateOrbitalObjectLocation(object, delta);
        } else {
            navigatorService.leaveOrbit(object);
            courseService.deleteById(activeCourse.getId());
            updateUnattachedObject(object, currentTimeMills, schedulerDuration);
        }
    }

    private void updateOrbitalObjectLocation(BasicObject object, Double delta) {
        double deltaAngleRadians = delta / object.getOrbitalPeriod();

        double angleDegrees = Math.toDegrees(object.getAngle()) < 0
                ? 360 + Math.toDegrees(object.getAngle())
                : Math.toDegrees(object.getAngle());
        double deltaAngleDegrees = Math.toDegrees(deltaAngleRadians);

        double objectAngleDegrees = object.getClockwiseRotation()
                ? angleDegrees - deltaAngleDegrees
                : angleDegrees + deltaAngleDegrees;

        int rounds = (int) objectAngleDegrees / 360;
        if (objectAngleDegrees <= 0) {
            objectAngleDegrees += 360.00 * (rounds + 1);
        } else if (objectAngleDegrees >= 360) {
            objectAngleDegrees -= 360.00 * (rounds + 1);
        }

        double angleRadians = Math.toRadians(objectAngleDegrees);
        object.setX((float) Math.cos(angleRadians) * object.getAphelion() + object.getPlanet().getX());
        object.setY((float) Math.sin(angleRadians) * object.getAphelion() + object.getPlanet().getY());
        object.setAngle((float) angleRadians);
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
        object.setX(determinePosition(object.getX(), object.getSpeedX(), staticMotionLength, 0.0));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), staticMotionLength, 0.0));
    }

    private void completeObjectCourses(Course activeCourse, BasicObject object, Long schedulerStartTime, Long schedulerDuration) {
        Instant endSchedulerInstant = Instant.ofEpochMilli(schedulerStartTime + schedulerDuration);
        long courseDuration;

        while (schedulerDuration > 0) {
            if (activeCourse.getPlanet() != null) {
                StarShip starship = starShipService.getById(object.getId());
                if (starShipEngine.isShipCanDockOrbit(starship, activeCourse.getPlanet())) {
                    navigatorService.attachToOrbit(object, activeCourse);

                    long flyDuration = activeCourse.getPrevious() == null
                            ? schedulerDuration
                            : Duration.between(Instant.ofEpochMilli(activeCourse.getPrevious().getExpireAt()), endSchedulerInstant).toMillis();

                    updateOrbitalObject(object, calculateDelta(flyDuration), schedulerStartTime, schedulerDuration);
                } else {
                    notificationEngine.sendCannotAttachToOrbitNotification(starship.getUser());
                    courseService.deleteById(activeCourse.getId());
                }
                break;
            }

//            courseDuration = calculateCourseDuration(activeCourse, schedulerDuration, Instant.ofEpochMilli(schedulerStartTime));

            if (activeCourse.getTime() >= schedulerDuration) {
                courseDuration = schedulerDuration;
                schedulerDuration = 0L;
            } else {
                courseDuration = activeCourse.getTime();
                schedulerDuration -= activeCourse.getTime();
            }
            activeCourse.setTime(activeCourse.getTime() - courseDuration);

            updateObjectFields(object, activeCourse, courseDuration);

            if (schedulerDuration == 0L) {
                if (activeCourse.getTime() == 0) {
                    courseService.deleteById(activeCourse.getId());
                }
                break;
            }

            if(activeCourse.hasNext()) {
                Course current = activeCourse;
                activeCourse = current.getNext();
                courseService.deleteById(current.getId());
                activeCourse.setPrevious(null);
            } else {
                staticObjectMotion(object, schedulerDuration);
                courseService.deleteById(activeCourse.getId());
                schedulerDuration = 0L;
            }

















//            Instant activeCourseExpireAtInstant = Instant.ofEpochMilli(activeCourse.getExpireAt());

//            if (activeCourse.hasNext() && activeCourseExpireAtInstant.isBefore(endSchedulerInstant)) {
//                activeCourse = activeCourse.getNext();
//                activeCourse.setExpireAt(activeCourse.getPrevious().getExpireAt() + activeCourse.getTime());
//
//            } else {
//                if (activeCourseExpireAtInstant.isBefore(endSchedulerInstant)) {
//                    long staticMotionDuration = Duration.between(activeCourseExpireAtInstant, endSchedulerInstant).toMillis();
//                    staticObjectMotion(object, staticMotionDuration);
//                }
//                break;
//            }
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

                activeCourse.setExpireAt(schedulerStartTime.toEpochMilli() + activeCourse.getTime());
                System.out.println("FIRST COURSE EXPIRE AT: " + activeCourse.getExpireAt());
//                System.out.println("FIRST COURSE CALCULATED TIME: " + (schedulerStartTime.toEpochMilli() - activeCourse.getExpireAt().toEpochMilli()));

            } else {

                if (Instant.ofEpochMilli(activeCourse.getExpireAt()).isBefore(endSchedulerInstant)) {
//                    courseDuration = Duration.between(schedulerStartTime, Instant.ofEpochMilli(activeCourse.getExpireAt())).toMillis();
                    courseDuration = activeCourse.getExpireAt() - schedulerStartTime.toEpochMilli();
                    System.out.println("221 " + courseDuration + "COURSE ID: " + activeCourse.getId());
                } else {
                    courseDuration = Duration.between(schedulerStartTime, endSchedulerInstant).toMillis();
                    System.out.println("222 " + courseDuration + " COURSE ID: " + activeCourse.getId());
                }
            }

        } else {
            long timeBetweenCourseAndEndScheduler = Duration.between(Instant.ofEpochMilli(activeCourse.getPrevious().getExpireAt()), endSchedulerInstant).toMillis();

            courseDuration = timeBetweenCourseAndEndScheduler > activeCourse.getTime()
                    ? activeCourse.getTime()
                    : timeBetweenCourseAndEndScheduler;
        }

//        System.out.println("COURSE ID: " + activeCourse.getId() + " COURSE DURATION: " + courseDuration);

        return courseDuration;
    }

    private void updateObjectFields(BasicObject object, Course activeCourse, Long courseDuration) {
        object.setX(determinePosition(object.getX(), object.getSpeedX(), courseDuration, activeCourse.getAccelerationX()));
        object.setY(determinePosition(object.getY(), object.getSpeedY(), courseDuration, activeCourse.getAccelerationY()));


        System.out.println("Course ID : " + activeCourse.getId());
        System.out.println("Course time : " + activeCourse.getTime());
        System.out.println("courseDuration : " + courseDuration);
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

    private Double determinePosition(Double coordinate, Double speed, Long time, Double acceleration) {
        double dividedTime = (time / 1000D) * appProperties.getTimeFlowModifier();
        double distanceCovered = (speed * dividedTime + (acceleration * Math.pow(dividedTime, 2)) / 2);

        return (coordinate + distanceCovered);
    }

    private Double calculateDelta(Long schedulerDuration) {

        return Math.PI * 2 * schedulerDuration * appProperties.getTimeFlowModifier() / (1000 * 60 * 60 * 24);
    }

    private Double calculateSpeed(Double speed, Double acceleration, long time) {
//        System.out.println("CALCULATING SPEED...");
//        return speed + (acceleration * time * appProperties.getTimeFlowModifier() / (1000 * 60 * 60));

//        System.out.println("CALCULATING SPEED...");
        double dividedTime = (time / 1_000D) * appProperties.getTimeFlowModifier();
        return (speed + (acceleration * dividedTime));

    }

    private Double round(Double value, int scale) {
        double temp = value * (Math.pow(10, scale));
        temp = Math.round(temp);
        return temp / (Math.pow(10, scale));
    }
}
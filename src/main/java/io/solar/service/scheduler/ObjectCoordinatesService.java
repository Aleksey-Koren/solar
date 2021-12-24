package io.solar.service.scheduler;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.service.UtilityService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObjectCoordinatesService {
    private final String POSITION_ITERATION_UTILITY_KEY = "position_iteration";

    @Value("${app.navigator.num_update_object}")
    private Integer amountReceivedObjects;

    private final UtilityService utilityService;
    private final BasicObjectRepository basicObjectRepository;

    @Transactional
    public void update() {
        String positionIteration = utilityService.getValue(POSITION_ITERATION_UTILITY_KEY, "1");
        long currentIteration = Long.parseLong(positionIteration);
        List<BasicObject> objects;

        while (!(objects = retrieveObjectsForUpdate(currentIteration)).isEmpty()) {

            updateObjects(objects, currentIteration);
            basicObjectRepository.saveAllAndFlush(objects);
        }

        utilityService.updateValueByKey(POSITION_ITERATION_UTILITY_KEY, String.valueOf(currentIteration + 1));
    }

    private List<BasicObject> retrieveObjectsForUpdate(long currentIteration) {

        return basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP),
                currentIteration,
                Pageable.ofSize(amountReceivedObjects)
        );
    }

    private void updateObjects(List<BasicObject> objects, long currentIteration) {
        long now = System.currentTimeMillis();
        Point zero = new Point(0f, 0f);
        objects.forEach(object -> {
            if(object.getPlanet() != null && object.getAphelion() != null && object.getAngle() != null && object.getOrbitalPeriod() != null) {
                Instant epoch = Instant.parse("2019, 11, 12, 0, 0, 0, 0");
                        Double delta = Math.PI * 2 * (now - epoch.toEpochMilli()) / (1000 * 60 * 60 * 24);

                double da = delta / object.getOrbitalPeriod();
                object.setAngle(object.getAngle() + (float) da);

                var absX = Math.cos(object.getAngle()) * object.getAphelion() + zero.x;
                var absY = Math.sin(object.getAngle()) * object.getAphelion() + zero.y;
                object.setX((float)absX);
                object.setY((float)absY);

            }else{
                long time = now - object.getPositionIterationTs();
                object.setX(determinePosition(object.getX(), object.getSpeedX(), time));
                object.setY(determinePosition(object.getY(), object.getSpeedY(), time));

                object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getAccelerationX(), time));
                object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getAccelerationY(), time));

                object.setPositionIterationTs(now);
                object.setPositionIteration(currentIteration + 1);
            }
        });
    }

    private Float determinePosition(Float coordinate, Float speed, Long time) {
        return coordinate + (speed * time / 3_600_000);
    }

    private Float calculateSpeed(Float speed, Float acceleration, long time) {
        return speed + (acceleration * time / 3_600_000);
    }

    @AllArgsConstructor
    class Point {
        private Float x;
        private Float y;
    }
}
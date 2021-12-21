package io.solar.service.scheduler;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.UtilityRepository;
import io.solar.service.UtilityService;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        List<BasicObject> objects = basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP), currentIteration, Pageable.ofSize(amountReceivedObjects)
        );

        updateObjects(objects, currentIteration);

        basicObjectRepository.saveAllAndFlush(objects);
    }

    private void updateObjects(List<BasicObject> objects, long currentIteration) {
        long now = System.currentTimeMillis();
        objects.forEach(object -> {
            long time = now - object.getPositionIterationTs();
            object.setX(determinePosition(object.getX(), object.getSpeedX(), time));
            object.setY(determinePosition(object.getY(), object.getSpeedY(), time));

            object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getAccelerationX(), time));
            object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getAccelerationY(), time));

            object.setPositionIterationTs(now);
            object.setPositionIteration(currentIteration + 1);
        });
    }

    private Float determinePosition(Float coordinate, Float speed, Long time) {
        return coordinate + (speed * time / 3_600_000);
    }

    private Float calculateSpeed(Float speed, Float acceleration, long time) {
        return speed + (acceleration * time / 3_600_000);
    }
}

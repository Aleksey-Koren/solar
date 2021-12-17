package io.solar.service.scheduler;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectType;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.UtilityRepository;
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

    private final UtilityRepository utilityRepository;
    private final BasicObjectRepository basicObjectRepository;

    @Transactional
    public void update() {

        String positionIteration = utilityRepository.getValue(POSITION_ITERATION_UTILITY_KEY)
                .orElseThrow(() -> new ServiceException("Cannot find utility key: ".concat(POSITION_ITERATION_UTILITY_KEY)));

        List<BasicObject> objects = basicObjectRepository.findObjectsToUpdateCoordinates(
                List.of(ObjectType.STATION, ObjectType.SHIP), Long.parseLong(positionIteration), Pageable.ofSize(amountReceivedObjects)
        );

        updateObjects(objects);

        basicObjectRepository.saveAllAndFlush(objects);
    }

    private void updateObjects(List<BasicObject> objects) {

        objects.forEach(object -> {
            object.setX(determinePosition(object.getX(), object.getSpeedX(), object.getPositionIterationTs()));
            object.setY(determinePosition(object.getY(), object.getSpeedY(), object.getPositionIterationTs()));

            object.setSpeedX(calculateSpeed(object.getSpeedX(), object.getPositionIterationTs(), object.getAccelerationX()));
            object.setSpeedY(calculateSpeed(object.getSpeedY(), object.getPositionIterationTs(), object.getAccelerationY()));

            object.setPositionIterationTs(System.currentTimeMillis());
            object.setPositionIteration(object.getPositionIteration() + 1);
        });
    }

    private Float determinePosition(Float coordinate, Float speed, Long positionIterationTs) {

        return coordinate + (speed * (System.currentTimeMillis() - positionIterationTs) / 3_600_000);
    }

    //todo: maybe change basicObject.speedX(Y) -> double
    private Float calculateSpeed(Float speed, Long positionIterationTs, Float acceleration) {

        return (float) (speed + (acceleration * Math.pow((System.currentTimeMillis() - positionIterationTs), 2)));
    }
}
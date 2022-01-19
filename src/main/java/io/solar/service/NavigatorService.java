package io.solar.service;

import io.solar.entity.Course;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.Math.*;
import static java.lang.Math.pow;

@Service
@RequiredArgsConstructor
public class NavigatorService {

    @Value("${app.navigator.max_distance}")
    private Float maxDistance;

    @Value("${app.navigator.max_speed}")
    private Float maxSpeed;

    private static final Float HARDCODED_ORBITAL_PERIOD = 1f;

    private final StationService stationService;
    private final StarShipService starshipService;

    public void dockShip(Long stationId, Long starshipId) {
        Station station = stationService.findById(stationId)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find station with id = %d", stationId)));

        StarShip starShip = starshipService.findById(starshipId)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find starship with id = %d", starshipId)));

        if (isShipCanDockWithStation(starShip, station)) {
            starshipService.dockShip(starShip, station);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot dock ship to station");
        }
    }

    public void undockShip(Long starshipId) {
        StarShip starShip = starshipService.findById(starshipId)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find starship with id = %d", starshipId)));

        starshipService.undockShip(starShip);
    }

    private boolean isShipCanDockWithStation(StarShip starship, Station station) {
        double distance = calcDistance(station, starship);

        return starship.getSpeed() < maxSpeed && distance < maxDistance;
    }

    private double calcDistance(BasicObject objectA, BasicObject objectB) {
        return sqrt(pow(objectB.getX() - objectA.getX(), 2) + pow(objectB.getY() - objectA.getY(), 2));
    }

    public void attachToOrbit(BasicObject object, Course activeCourse) {
        object.setAngle((float) calcAngle(object, activeCourse.getPlanet()));
        object.setAphelion((float) calcDistance(activeCourse.getPlanet(), object));
        object.setPlanet(activeCourse.getPlanet());
        object.setOrbitalPeriod(HARDCODED_ORBITAL_PERIOD);
        object.setAccelerationX(0f);
        object.setAccelerationY(0f);
        object.setSpeedX(0f);
        object.setSpeedY(0f);
    }

    private double calcAngle(BasicObject atOrbit, BasicObject atCenter) {
        float relativeX = atOrbit.getX() - atCenter.getX();
        float relativeY = atOrbit.getY() - atCenter.getY();
        double angle = atan2(relativeY, relativeX);
        return angle >= 0 ? angle : PI * 2 + angle;
    }

    public void leaveOrbit(BasicObject object) {
        object.setAngle(null);
        object.setAphelion(null);
        object.setPlanet(null);
        object.setOrbitalPeriod(null);
        object.setSpeedX(0f);
        object.setSpeedY(0f);
        object.setAccelerationX(0f);
        object.setAccelerationY(0f);
    }
}
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

    private double calcDistance(BasicObject station, BasicObject starShip) {
        return Math.sqrt(pow(starShip.getX() - station.getX(), 2) + pow(starShip.getY() - station.getY(), 2));
    }

    public void SetOrbitParameters(BasicObject object, Course activeCourse) {
        object.setAngle((float) calcAngle(object, activeCourse.getPlanet()));
        object.setAphelion((float) calcDistance(activeCourse.getPlanet(), object));
        object.setPlanet(activeCourse.getPlanet());
        object.setOrbitalPeriod(HARDCODED_ORBITAL_PERIOD);
    }

    private double calcAngle(BasicObject orbital, BasicObject center) {
        float relativeX = orbital.getX() - center.getPlanet().getX();
        float relativeY = orbital.getY() - center.getPlanet().getY();
        double angle = Math.atan2(relativeY, relativeX);
        return angle >= 0 ? angle : Math.PI * 2 + angle;
    }
}
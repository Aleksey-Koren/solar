package io.solar.service;

import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.service.exception.ServiceException;
import io.solar.service.exception.ShipDockException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.lang.Math.pow;

@Service
@RequiredArgsConstructor
public class NavigatorService {

    @Value("${app.navigator.max_distance}")
    private Float maxDistance;

    @Value("${app.navigator.max_speed}")
    private Float maxSpeed;

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
            throw new ShipDockException();
        }
    }

    public void undockShip(Long starshipId) {
        StarShip starShip = starshipService.findById(starshipId)
                .orElseThrow(() -> new ServiceException(String.format("Cannot find starship with id = %d", starshipId)));

        starshipService.undockShip(starShip);
    }

    private boolean isShipCanDockWithStation(StarShip starship, Station station) {
        Double distance = calcDistance(station, starship);

        return starship.getSpeed() < maxSpeed && distance < maxDistance;
    }

    private Double calcDistance(Station station, StarShip starShip) {

        return Math.sqrt(pow(starShip.getX() - station.getX(), 2) + pow(starShip.getY() - station.getY(), 2));
    }

}
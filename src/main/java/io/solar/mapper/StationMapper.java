package io.solar.mapper;

import io.solar.dto.StationDto;
import io.solar.entity.objects.Station;
import io.solar.service.ObjectTypeDescriptionService;
import io.solar.service.PlanetService;
import io.solar.service.ServiceException;
import io.solar.service.StationService;
import io.solar.utils.server.beans.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StationMapper {

    private StationService stationService;
    private ObjectTypeDescriptionService objectTypeDescriptionService;
    private PlanetService planetService;

    @Autowired
    public StationMapper(StationService stationService, ObjectTypeDescriptionService objectTypeDescriptionService, PlanetService planetService) {
        this.stationService = stationService;
        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.planetService = planetService;
    }

    public Station toEntity(StationDto dto) {
        Station station;
        if (dto.getId() != null) {
            station = stationService.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no station with such id in database"));
        }else{
            station = new Station();
        }

        station.setId(dto.getId());
        station.setTitle(dto.getTitle());
        station.setPlanet(dto.getPlanetId() != null ? planetService.findById(dto.getPlanetId()) : null);
        station.setPopulation(dto.getPopulation());
        station.setFraction(dto.getFraction());
        station.setX(dto.getX());
        station.setY(dto.getY());
        station.setAphelion(dto.getAphelion());
        station.setAngle(dto.getAngle());
        station.setOrbitalPeriod(dto.getOrbitalPeriod());
        station.setObjectTypeDescription(objectTypeDescriptionService.findById(dto.getObjectTypeDescriptionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no ObjectTypeDescription with such id in database")));

        return station;
    }

    public StationDto toDto(Station station) {
        StationDto dto = new StationDto();

        dto.setId(station.getId());
        dto.setTitle(station.getTitle());
        dto.setPlanetId(station.getPlanet() != null ? station.getPlanet().getId() : null);
        dto.setPopulation(station.getPopulation());
        dto.setFraction(dto.getFraction());
        dto.setX(station.getX());
        dto.setY(station.getY());
        dto.setAphelion(station.getAphelion());
        dto.setAngle(station.getAngle());
        dto.setOrbitalPeriod(station.getOrbitalPeriod());
        if(station.getObjectTypeDescription() == null) {
            throw new ServiceException("Station must not exist without an ObjectTypeDescription field");
        }
        dto.setObjectTypeDescriptionId(station.getObjectTypeDescription().getId());

        return dto;
    }
}
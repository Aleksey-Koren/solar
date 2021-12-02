package io.solar.mapper;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.StationDto;
import io.solar.entity.objects.Station;
import io.solar.mapper.objects.BasicObjectMapper;
import io.solar.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.stream.Collectors.*;

@Service
public class StationMapper {

    private final StationService stationService;
    private final ObjectTypeDescriptionService objectTypeDescriptionService;
    private final PlanetService planetService;
    private final BasicObjectService basicObjectService;
    private final BasicObjectMapper basicObjectMapper;

    @Autowired
    public StationMapper(StationService stationService,
                         ObjectTypeDescriptionService objectTypeDescriptionService,
                         PlanetService planetService,
                         BasicObjectService basicObjectService,
                         BasicObjectMapper basicObjectMapper
                         ) {
        this.stationService = stationService;
        this.objectTypeDescriptionService = objectTypeDescriptionService;
        this.planetService = planetService;
        this.basicObjectService = basicObjectService;
        this.basicObjectMapper = basicObjectMapper;
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
        station.setPlanet(dto.getPlanet() != null ? planetService.findById(dto.getPlanet()) : null);
        station.setPopulation(dto.getPopulation());
        station.setFraction(dto.getFraction());
        station.setX(dto.getX());
        station.setY(dto.getY());
        station.setAphelion(dto.getAphelion());
        station.setAngle(dto.getAngle());
        station.setOrbitalPeriod(dto.getOrbitalPeriod());
        station.setObjectTypeDescription(objectTypeDescriptionService.findById(dto.getHullId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no ObjectTypeDescription with such id in database")));

//        station.setProduction(dto.getProduction());

        station.setAttachedObjects(dto.getAttachedObjects() != null
                ?
                dto.getAttachedObjects().stream()
                        .map(BasicObjectViewDto::getId)
                        .map(s -> basicObjectService.findById(s)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no Object with such id in database")))
                        .collect(toList())
                : null);

        return station;
    }

    public StationDto toDto(Station station) {
        StationDto dto = new StationDto();

        dto.setId(station.getId());
        dto.setTitle(station.getTitle());
        dto.setPlanet(station.getPlanet() != null ? station.getPlanet().getId() : null);
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
        dto.setHullId(station.getObjectTypeDescription().getId());
        dto.setAttachedObjects(station.getAttachedObjects() != null ?
                station.getAttachedObjects().stream().map(basicObjectMapper::toBasicObjectViewDto).collect(toList())
                : null);
        return dto;
    }
}
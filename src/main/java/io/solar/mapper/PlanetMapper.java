package io.solar.mapper;

import io.solar.dto.PlanetDto;
import io.solar.entity.Planet;
import io.solar.repository.PlanetRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@NoArgsConstructor
public class PlanetMapper {

    private PlanetRepository planetRepository;

    @Autowired
    public PlanetMapper(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    public Planet toEntity(PlanetDto dto) {
        Planet entity;

        if (dto.getId() != null) {
            entity = planetRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such planet ID in database"));
        } else {
            entity = new Planet();
        }


        entity.setX(dto.getX());
        entity.setY(dto.getY());
        entity.setPlanet(dto.getParent() != null ? planetRepository.findById(dto.getParent()).orElseThrow() : null);
        entity.setPopulation(dto.getPopulation());
        entity.setAldebo(dto.getAldebo());
        entity.setAphelion(dto.getAphelion());
        entity.setAxialTilt(dto.getAxialTilt());
        entity.setEccentricity(dto.getEccentricity());
        entity.setEscapeVelocity(dto.getEscapeVelocity());
        entity.setInclination(dto.getInclination());
        entity.setMass(dto.getMass());
        entity.setMeanAnomaly(dto.getMeanAnomaly());
        entity.setMeanOrbitRadius(dto.getMeanOrbitRadius());
        entity.setMeanRadius(dto.getMeanRadius());
        entity.setTitle(dto.getTitle());
        entity.setOrbitalPeriod(dto.getOrbitalPeriod());
        entity.setPerihelion(dto.getPerihelion());
        entity.setSiderealRotationPeriod(dto.getSiderealRotationPeriod());
        entity.setSurfaceGravity(dto.getSurfaceGravity());
        entity.setSurfacePressure(dto.getSurfacePressure());
        entity.setPlanetVolume(dto.getVolume());
        entity.setAngle(dto.getAngle());
        entity.setType(dto.getType());

        return entity;
    }

    public PlanetDto toDto(Planet entity) {

        return PlanetDto.builder()
                .id(entity.getId())
                .aldebo(entity.getAldebo())
                .aphelion(entity.getAphelion())
                .axialTilt(entity.getAxialTilt())
                .eccentricity(entity.getEccentricity())
                .escapeVelocity(entity.getEscapeVelocity())
                .inclination(entity.getInclination())
                .mass(entity.getMass())
                .meanAnomaly(entity.getMeanAnomaly())
                .meanOrbitRadius(entity.getMeanOrbitRadius())
                .meanRadius(entity.getMeanRadius())
                .title(entity.getTitle())
                .orbitalPeriod(entity.getOrbitalPeriod())
                .perihelion(entity.getPerihelion())
                .siderealRotationPeriod(entity.getSiderealRotationPeriod())
                .surfaceGravity(entity.getSurfaceGravity())
                .surfacePressure(entity.getSurfacePressure())
                .volume(entity.getPlanetVolume())
                .angle(entity.getAngle())
                .type(entity.getType())
                .x(entity.getX())
                .y(entity.getY())
                .population(entity.getPopulation())
                .parent(entity.getPlanet() != null ? entity.getPlanet().getId() : null)
                .build();
    }
}
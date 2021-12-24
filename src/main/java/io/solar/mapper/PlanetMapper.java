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
    private UserMapper userMapper;

    @Autowired
    public PlanetMapper(PlanetRepository planetRepository, UserMapper userMapper) {
        this.planetRepository = planetRepository;
        this.userMapper = userMapper;
    }

    public Planet toEntity(PlanetDto dto) {
        Planet entity;

        if (dto.getId() != null) {
            entity = planetRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such planet ID in database"));
        }else{
            entity = new Planet();
        }

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
        entity.setVolume(dto.getVolume());
        entity.setParent(planetRepository.findById(dto.getParent()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        String.format("There is no planet with id = %d in database", dto.getParent())
                )));
        entity.setAngle(dto.getAngle());
        entity.setType(dto.getType());
        entity.setPositionIterationTs(dto.getPositionIterationTs());

        return entity;
    }

    public PlanetDto toDto (Planet entity) {

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
                .volume(entity.getVolume())
                .parent(entity.getParent() != null ? entity.getParent().getId() : null)
                .angle(entity.getAngle())
                .type(entity.getType())
                .positionIterationTs(entity.getPositionIterationTs())
                .build();
    }
}
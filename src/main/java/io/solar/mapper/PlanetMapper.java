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
        Planet planet;
        if (dto.getId() != null) {
            planet = planetRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no such planet ID in database"));
        }else{
            planet = new Planet();
        }

        planet.setAldebo(dto.getAldebo());
        planet.setAphelion(dto.getAphelion());
        planet.setAxialTilt(dto.getAxialTilt());
        planet.setEccentricity(dto.getEccentricity());
        planet.setEscapeVelocity(dto.getEscapeVelocity());
        planet.setInclination(dto.getInclination());
        planet.setMass(dto.getMass());
        planet.setMeanAnomaly(dto.getMeanAnomaly());
        planet.setMeanOrbitRadius(dto.getMeanOrbitRadius());
        planet.setMeanRadius(dto.getMeanRadius());
        planet.setTitle(dto.getTitle());
        planet.setOrbitalPeriod(dto.getOrbitalPeriod());
        planet.setPerihelion(dto.getPerihelion());
        planet.setSiderealRotationPeriod(dto.getSiderealRotationPeriod());
        planet.setSurfaceGravity(dto.getSurfaceGravity());
        planet.setSurfacePressure(dto.getSurfacePressure());
        planet.setVolume(dto.getVolume());
        planet.setParent(dto.getParent());
        planet.setAngle(dto.getAngle());
        planet.setType(dto.getType());

        return planet;
    }

    public PlanetDto toDto (Planet planet) {
        PlanetDto dto = new PlanetDto();

        dto.setId(planet.getId());
        dto.setAldebo(planet.getAldebo());
        dto.setAphelion(planet.getAphelion());
        dto.setAxialTilt(planet.getAxialTilt());
        dto.setEccentricity(planet.getEccentricity());
        dto.setEscapeVelocity(planet.getEscapeVelocity());
        dto.setInclination(planet.getInclination());
        dto.setMass(planet.getMass());
        dto.setMeanAnomaly(planet.getMeanAnomaly());
        dto.setMeanOrbitRadius(planet.getMeanOrbitRadius());
        dto.setMeanRadius(planet.getMeanRadius());
        dto.setTitle(planet.getTitle());
        dto.setOrbitalPeriod(planet.getOrbitalPeriod());
        dto.setPerihelion(planet.getPerihelion());
        dto.setSiderealRotationPeriod(planet.getSiderealRotationPeriod());
        dto.setSurfaceGravity(planet.getSurfaceGravity());
        dto.setSurfacePressure(planet.getSurfacePressure());
        dto.setVolume(planet.getVolume());
        dto.setParent(planet.getParent());
        dto.setAngle(planet.getAngle());
        dto.setType(planet.getType());

        return dto;
    }
}
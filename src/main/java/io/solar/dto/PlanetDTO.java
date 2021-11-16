package io.solar.dto;

import io.solar.entity.Planet;
import lombok.Builder;

@Builder
public class PlanetDTO {

    Long id;
    Float aldebo;
    Long aphelion;
    String axialTilt;
    String eccentricity;

    String escapeVelocity;
    String inclination;
    String mass;
    Float meanAnomaly;
    String meanOrbitRadius;
    String meanRadius;

    String title;
    String orbitalPeriod;
    String perihelion;
    String siderealRotationPeriod;
    String surfaceGravity;
    String surfacePressure;
    String volume;
    Long parent;
    Float angle;
    String type;

    public PlanetDTO (Planet planet) {
        this.id = planet.getId();
        this.aldebo = planet.getAldebo();
        this.aphelion = planet.getAphelion();
        this.axialTilt = planet.getAxialTilt();
        this.eccentricity = planet.getEccentricity();
        this.escapeVelocity = planet.getEscapeVelocity();
        this.inclination = planet.getInclination();
        this.mass = planet.getMass();
        this.meanAnomaly = planet.getMeanAnomaly();
        this.meanOrbitRadius = planet.getMeanOrbitRadius();
        this.meanRadius = planet.getMeanRadius();
        this.title = planet.getTitle();
        this.orbitalPeriod = planet.getOrbitalPeriod();
        this.perihelion = planet.getPerihelion();
        this.siderealRotationPeriod = planet.getSiderealRotationPeriod();
        this.surfaceGravity = planet.getSurfaceGravity();
        this.surfacePressure = planet.getSurfacePressure();
        this.volume = planet.getVolume();
        this.parent = planet.getParent();
        this.angle = planet.getAngle();
        this.type = planet.getType();
    }

    public PlanetDTO(Long id, Float aldebo, Long aphelion, String axialTilt, String eccentricity, String escapeVelocity, String inclination, String mass, Float meanAnomaly, String meanOrbitRadius, String meanRadius, String title, String orbitalPeriod, String perihelion, String siderealRotationPeriod, String surfaceGravity, String surfacePressure, String volume, Long parent, Float angle, String type) {
        this.id = id;
        this.aldebo = aldebo;
        this.aphelion = aphelion;
        this.axialTilt = axialTilt;
        this.eccentricity = eccentricity;
        this.escapeVelocity = escapeVelocity;
        this.inclination = inclination;
        this.mass = mass;
        this.meanAnomaly = meanAnomaly;
        this.meanOrbitRadius = meanOrbitRadius;
        this.meanRadius = meanRadius;
        this.title = title;
        this.orbitalPeriod = orbitalPeriod;
        this.perihelion = perihelion;
        this.siderealRotationPeriod = siderealRotationPeriod;
        this.surfaceGravity = surfaceGravity;
        this.surfacePressure = surfacePressure;
        this.volume = volume;
        this.parent = parent;
        this.angle = angle;
        this.type = type;
    }
}
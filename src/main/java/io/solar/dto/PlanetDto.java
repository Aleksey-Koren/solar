package io.solar.dto;

import io.solar.entity.Planet;
import lombok.Data;


@Data
public class PlanetDto {

    private Long id;
    private Float aldebo;
    private Long aphelion;
    private String axialTilt;
    private String eccentricity;
    private String escapeVelocity;
    private String inclination;
    private String mass;
    private Float meanAnomaly;
    private String meanOrbitRadius;
    private String meanRadius;
    private String title;
    private String orbitalPeriod;
    private String perihelion;
    private String siderealRotationPeriod;
    private String surfaceGravity;
    private String surfacePressure;
    private String volume;
    private Long parent;
    private Float angle;
    private String type;

    public PlanetDto() {

    }

    public PlanetDto(Planet planet) {
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
}
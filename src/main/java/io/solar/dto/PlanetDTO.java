package io.solar.dto;

import io.solar.entity.Planet;

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
}
package io.solar.dto;

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
}
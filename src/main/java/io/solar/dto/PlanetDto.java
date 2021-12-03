package io.solar.dto;

import lombok.Data;

@Data
public class PlanetDto {

    private Long id;
    private Float aldebo;
    private Float aphelion; //TODO it was Long.  Might be that it will bring some troubles at frontend)))
    private String axialTilt;
    private String eccentricity;
    private String escapeVelocity;
    private String inclination;
    private String mass;
    private Float meanAnomaly;
    private String meanOrbitRadius;
    private String meanRadius;
    private String title;
    private Float orbitalPeriod; //TODO It was String. Might be that it will bring some troubles at frontend)))
    private String perihelion;
    private String siderealRotationPeriod;
    private String surfaceGravity;
    private String surfacePressure;
    private String volume;
    private Long parent;
    private Float angle;
    private String type;
}
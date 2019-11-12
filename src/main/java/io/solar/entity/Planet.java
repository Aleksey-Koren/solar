package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Planet {
    Long id;
    Float aldebo;
    Long aphelion;
    String axialTilt;
    String eccentricity;
    String escapeVelocity;
    String inclination;
    String mass;
    Float meanAnomaly;
    Float angle;
    String meanOrbitRadius;
    String meanRadius;
    String title;
    String type;
    String orbitalPeriod;
    String perihelion;
    String siderealRotationPeriod;
    String surfaceGravity;
    String surfacePressure;
    String volume;
    Long parent;
}

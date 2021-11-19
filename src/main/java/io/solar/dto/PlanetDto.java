package io.solar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.solar.entity.Planet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    List<UserDto> users;

}
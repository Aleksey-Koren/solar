package io.solar.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    Long parent; //TODO If this is planetId for the Moon, we should change it to Entity
    Float angle;
    String type;
    @OneToMany(mappedBy = "planet")
    List<User> users;
}
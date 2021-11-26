package io.solar.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "planets")
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
    Long parent;
    Float angle;
    String type;
    @OneToMany(mappedBy = "planet")
    List<User> users;
}
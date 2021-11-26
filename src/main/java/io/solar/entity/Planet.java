package io.solar.entity;

import io.solar.entity.objects.AbstractObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.ObjectInputFilter;
import java.util.List;

@Data
@Entity
@Table(name = "planets")
@EqualsAndHashCode(callSuper = true)
public class Planet extends AbstractObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float aldebo;
//    private Long aphelion;
    private String axialTilt;
    private String eccentricity;
    private String escapeVelocity;
    private String inclination;
    private String mass;
    private Float meanAnomaly;
    private String meanOrbitRadius;
    private String meanRadius;
//    private String title;
//    private String orbitalPeriod;
    private String perihelion;
    private String siderealRotationPeriod;
    private String surfaceGravity;
    private String surfacePressure;
    private String volume;
    private Long parent;
//    private Float angle;
    private String type;
    @OneToMany(mappedBy = "planet")
    List<User> users;
}
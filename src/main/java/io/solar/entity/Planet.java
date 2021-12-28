package io.solar.entity;

import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.Station;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "planets")
public class Planet extends BasicObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float aldebo;
    private String axialTilt;
    private String eccentricity;
    private String escapeVelocity;
    private String inclination;
    private String mass;
    private Float meanAnomaly;
    private String meanOrbitRadius;
    private String meanRadius;
    private String perihelion;
    private String siderealRotationPeriod;
    private String surfaceGravity;
    private String surfacePressure;
    private String volume;
    private String type;

    @OneToMany(mappedBy = "planet")
    @EqualsAndHashCode.Exclude
    List<Station> stations;

    @Override
    public String toString() {
        return "Planet{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
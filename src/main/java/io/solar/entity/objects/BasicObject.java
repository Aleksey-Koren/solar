package io.solar.entity.objects;

import io.solar.entity.Planet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class BasicObject implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet")
    protected Planet planet;
    protected Long population;
    protected String fraction;
    protected String title;
    protected Float x;
    protected Float y;
    protected Float aphelion;
    protected Float orbitalPeriod;
    protected Float angle;
    protected Float rotationAngle;
    @ManyToOne
    @JoinColumn(name = "hull_id")
    protected ObjectTypeDescription objectTypeDescription;
    protected Long userId;
    protected Boolean active;
    protected Long durability;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "attached_to_ship")
    protected BasicObject attachedToShip;
    protected Long attachedToSocket;
    @Enumerated(EnumType.STRING)
    protected ObjectStatus status;
    protected Float speedX;
    protected Float speedY;
    protected Float accelerationX;
    protected Float accelerationY;


    @OneToMany(mappedBy = "attachedToShip", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @EqualsAndHashCode.Exclude
    protected List<BasicObject> attachedObjects;

    @Transient
    public Double getSpeed() {
        return Math.sqrt(Math.pow(this.speedX, 2) + Math.pow(this.speedY, 2));
    }

    @Transient
    public Double getAcceleration() {
        return Math.sqrt(Math.pow(this.accelerationX, 2) + Math.pow(this.accelerationY, 2));
    }
}
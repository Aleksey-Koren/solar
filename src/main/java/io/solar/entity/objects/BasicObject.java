package io.solar.entity.objects;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BasicObject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    protected Boolean active;

    protected Integer durability;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "attached_to_ship")
    protected BasicObject attachedToShip;

    protected Long attachedToSocket;

    @Enumerated(EnumType.STRING)
    protected ObjectStatus status;

    @Column(name = "speed_x")
    protected Float speedX;

    @Column(name = "speed_y")
    protected Float speedY;

    @Column(name = "acceleration_x")
    protected Float accelerationX;

    @Column(name = "acceleration_y")
    protected Float accelerationY;

    @Column(name = "position_iteration")
    protected Long positionIteration;

    @Column(name = "position_iteration_ts")
    protected Long positionIterationTs;

    @Column(name = "clockwise_rotation")
    protected Boolean clockwiseRotation;

    @Column(name = "volume")
    protected Float volume;

    @OneToMany(mappedBy = "attachedToShip", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    protected List<BasicObject> attachedObjects;

    @OneToMany(mappedBy = "object", cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    protected List<Course> courses;

    @Transient
    public Double getSpeed() {
        return Math.sqrt(Math.pow(this.speedX, 2) + Math.pow(this.speedY, 2));
    }

}
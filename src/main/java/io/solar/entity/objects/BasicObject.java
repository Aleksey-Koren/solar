package io.solar.entity.objects;

import io.solar.entity.Course;
import io.solar.entity.Planet;
import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.modification.Modification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
    protected Double x;
    protected Double y;
    protected Float aphelion;
    protected Float orbitalPeriod;
    protected Float angle;
    protected Float rotationAngle;

    @ManyToOne
    @JoinColumn(name = "hull_id")
    protected ObjectTypeDescription objectTypeDescription;

    protected Boolean active;

    protected Integer durability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attached_to_ship")
    protected BasicObject attachedToShip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attached_to_socket")
    protected InventorySocket attachedToSocket;

    @Enumerated(EnumType.STRING)
    protected ObjectStatus status;

    @Column(name = "speed_x")
    protected Double speedX;

    @Column(name = "speed_y")
    protected Double speedY;

    @Column(name = "acceleration_x")
    protected Double accelerationX;

    @Column(name = "acceleration_y")
    protected Double accelerationY;

    @Column(name = "position_iteration")
    protected Long positionIteration;

    @Column(name = "position_iteration_ts")
    protected Long positionIterationTs;

    @Column(name = "clockwise_rotation")
    protected Boolean clockwiseRotation;

    @Column(name = "volume")
    protected Float volume;

    @Column(name = "energy_consumption")
    protected Long energyConsumption;

    protected Boolean isEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modification_id")
    private Modification modification;

    @OneToMany(mappedBy = "attachedToShip")
    @EqualsAndHashCode.Exclude
    protected List<BasicObject> attachedObjects;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "spaceTech")
    private List<SpaceTechSocket> sockets;

    @OneToMany(mappedBy = "object", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    protected List<Course> courses;

    @Transient
    public Double getSpeed() {
        return Math.sqrt(Math.pow(this.speedX, 2) + Math.pow(this.speedY, 2));
    }
}
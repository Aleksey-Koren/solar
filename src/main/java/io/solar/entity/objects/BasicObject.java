package io.solar.entity.objects;

import io.solar.entity.Planet;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
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
    protected Float acceleration;
    protected Float speed;

    @OneToMany(mappedBy = "attachedToShip", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @EqualsAndHashCode.Exclude
    protected List<BasicObject> attachedObjects;
}
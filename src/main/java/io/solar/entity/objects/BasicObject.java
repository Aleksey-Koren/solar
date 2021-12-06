package io.solar.entity.objects;

import io.solar.entity.Planet;
import lombok.Data;

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
import java.util.List;

@Data
@Entity
@Table(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class BasicObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "planet")
    private Planet planet;
    private Long population;
    private String fraction;
    private String title;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;
    @ManyToOne
    @JoinColumn(name = "hull_id")
    private ObjectTypeDescription objectTypeDescription;
    private Long userId;
    private Boolean active;
    private Long durability;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attached_to_ship")
    private BasicObject attachedToShip;
    private Long attachedToSocket;
    @Enumerated(EnumType.STRING)
    private ObjectStatus status;
    private Float acceleration;
    private Float speed;

    @OneToMany(mappedBy = "attachedToShip")
    private List<BasicObject> attachedObjects;

    //TODO ??????
//    private List<InventorySocket> socketList;

}
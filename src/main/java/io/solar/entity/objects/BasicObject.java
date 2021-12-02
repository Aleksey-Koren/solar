package io.solar.entity.objects;

import io.solar.entity.Planet;
import io.solar.entity.inventory.InventorySocket;
import lombok.Data;

import javax.persistence.*;
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
    private StarShip attachedToShip;
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
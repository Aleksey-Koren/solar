package io.solar.entity.objects;

import io.solar.entity.Planet;
import lombok.Data;

import javax.persistence.*;

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
    private Long attachedToShip;
    private Long attachedToSocket;
    @Enumerated(EnumType.STRING)
    private ObjectStatus status;
    private Float acceleration;
    private Float speed;

    //TODO ??????
//    private List<InventorySocket> socketList;
//    private List<ObjectItem> attachedObjects;
}
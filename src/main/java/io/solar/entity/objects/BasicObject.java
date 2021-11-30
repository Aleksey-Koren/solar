package io.solar.entity.objects;

import io.solar.entity.inventory.InventorySocket;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class BasicObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long planet;
    private Long population;
    private String fraction;
    private String title;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;

//    private Long hullId;
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
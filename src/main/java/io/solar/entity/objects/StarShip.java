package io.solar.entity.objects;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "star_ships")
@PrimaryKeyJoinColumn(name = "id")
public class StarShip extends BasicObject {

//TODO All this fields will be in ObjectTypeDescription field of BaseObject

//    private String hullTitle;
//    private Float powerDegradation;
//    private Float energyConsumption;
//    private Long maxDurability;
//    private String hullDescription;
}

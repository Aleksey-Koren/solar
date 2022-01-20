package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.interfaces.SpaceTech;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "star_ships")
@PrimaryKeyJoinColumn(name = "id")
public class StarShip extends BasicObject implements SpaceTech {

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Goods> goods;

//TODO All this fields would be in ObjectTypeDescription field of BaseObject

//    private String hullTitle;
//    private Float powerDegradation;
//    private Float energyConsumption;
//    private Long maxDurability;
//    private String hullDescription;
}

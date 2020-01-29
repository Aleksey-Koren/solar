package io.solar.entity.objects;

import io.solar.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StarShip extends AbstractObject {

    private String hullTitle;

    private Float powerDegradation;
    private Float energyConsumption;
    private Long maxDurability;
    private String hullDescription;

}

package io.solar.entity.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StarShip extends BasicObject {

    private String hullTitle;

    private Float powerDegradation;
    private Float energyConsumption;
    private Long maxDurability;
    private String hullDescription;

}

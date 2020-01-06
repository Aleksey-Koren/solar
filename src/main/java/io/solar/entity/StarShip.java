package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StarShip {

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
    private Long durability;
    private Long hullId;
    private String type;
    private Long userId;
    private Boolean active;
    private String hullTitle;
    private Float powerDegradation;
    private Float energyConsumption;
    private Long maxDurability;
    private String hullDescription;

    private List<Inventory> inventory;
}

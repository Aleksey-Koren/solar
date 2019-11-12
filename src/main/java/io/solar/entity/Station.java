package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Station {
    private Long id;
    private Long planetId;
    private Planet planet;
    private Long population;
    private String title;
    private String fraction;
    private String type;
    private Float x;
    private Float y;
    private Float aphelion;
    private Float orbitalPeriod;
    private Float angle;
    private List<Production> production;
    private List<Goods> goods;
}

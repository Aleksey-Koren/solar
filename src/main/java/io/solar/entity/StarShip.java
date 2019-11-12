package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StarShip {
    private Long id;
    private Long userId;
    private Long starShipType;
    private List<Inventory> inventory;
}

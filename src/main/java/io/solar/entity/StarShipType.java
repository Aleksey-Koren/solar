package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StarShipType {
    private Long id;
    private String title;
    private List<Slot> slots;
}

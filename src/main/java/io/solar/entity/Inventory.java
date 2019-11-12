package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Inventory {
    private Long id;
    private String title;
    private Long inventoryType;
    private Long slotId;
}

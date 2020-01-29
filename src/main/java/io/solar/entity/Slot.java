package io.solar.entity;

import io.solar.entity.inventory.InventoryType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Slot {
    private Long id;
    private String title;
    private List<InventoryType> types;
}

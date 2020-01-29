package io.solar.entity.inventory;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * hull, armor, shield generator
 */
@Getter
@Setter
public class InventoryType {
    private Long id;
    private String title;
    //pseudo field, used when inventory type used as many-to-many
    private String alias;
}

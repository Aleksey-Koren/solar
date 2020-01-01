package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * modification for item, such as
 * +50% evasion from ray weapon
 * +20% damage to shields
 * +damage devices
 */
@Getter
@Setter
public class InventoryModification {
    private Long id;
    private String title;
    private String data;
}

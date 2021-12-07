package io.solar.entity.objects;

import io.solar.entity.inventory.InventoryType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "object_type_description")
public class ObjectTypeDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "inventory_type")
    private Integer inventoryTypeId;

    @Column(name = "title")
    private String title;

    @Column(name = "power_min")
    private Float powerMin;

    @Column(name = "power_max")
    private Float powerMax;

    @Column(name = "power_degradation")
    private Float powerDegradation;

    @Column(name = "cooldown")
    private Float cooldown;

    @Column(name = "distance")
    private Float distance;

    @Column(name = "energy_consumption")
    private Integer energyConsumption;

    @Column(name = "durability")
    private Integer durability;

    @Column(name = "description")
    private String description;

    @Column(name = "mass")
    private Integer mass;

    @Column(name = "price")
    private Integer price;

    @Enumerated(EnumType.STRING)
    private ObjectType type;

    @Enumerated(EnumType.STRING)
    private ObjectSubType subType;
}

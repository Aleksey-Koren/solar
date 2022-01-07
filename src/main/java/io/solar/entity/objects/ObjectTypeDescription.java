package io.solar.entity.objects;

import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.inventory.InventoryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "object_type_description")
public class ObjectTypeDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inventory_type")
    private InventoryType inventoryType;

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

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<InventorySocket> socketList;
}

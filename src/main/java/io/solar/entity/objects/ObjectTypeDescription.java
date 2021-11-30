package io.solar.entity.objects;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "object_type_description")
public class ObjectTypeDescription {

    @Id
    private Integer id;
    private Integer inventoryType;
    private String title;
    private Float power_min;
    private Float power_max;
    private Float power_degradation;
    private Float cooldown;
    private Float distance;
    private Integer energy_consumption;
    private Integer durability;
    private String description;
    private Integer mass;
    private Integer price;
    @Enumerated(EnumType.STRING)
    private ObjectType type;
    @Enumerated(EnumType.STRING)
    private ObjectSubType subType;
}

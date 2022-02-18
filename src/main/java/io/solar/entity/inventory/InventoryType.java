package io.solar.entity.inventory;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * hull, armor, shield generator
 */
@Data
@Entity
@Table(name = "object_types")
public class InventoryType {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;
//        pseudo field, used when inventory type used as many-to-many
//    private String alias;
}
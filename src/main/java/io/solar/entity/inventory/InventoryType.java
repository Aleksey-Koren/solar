package io.solar.entity.inventory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

/**
 * hull, armor, shield generator
 */
@Data
@Entity
@Table (name = "object_types")
public class InventoryType {
    @Id
    @GeneratedValue
    private Long id;
    private String title;


//        pseudo field, used when inventory type used as many-to-many
//    private String alias;
}

package io.solar.entity.inventory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * hull, armor, shield generator
 */
@Data
@Entity
@Table (name = "object_types")
public class InventoryType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;


//        pseudo field, used when inventory type used as many-to-many
//    private String alias;
}

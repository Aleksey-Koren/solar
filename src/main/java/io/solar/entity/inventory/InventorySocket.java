package io.solar.entity.inventory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventorySocket {
     private Long id;
     private Long itemId;
     private Long itemTypeId;
     private String alias;
     private Integer sortOrder;


     public String toString() {
          return "Socket " + (id == null ? " new;" : " id: " + id + ";") + (alias == null ? "" : " " + alias);
     }
}

package io.solar.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class InventorySocketDto {
     private Long id;
     private Long itemId;
     private Long itemTypeId;
     private String alias;
     private Integer sortOrder;

     public String toString() {
          return "Socket " + (id == null ? " new;" : " id: " + id + ";") + (alias == null ? "" : " " + alias);
     }
}

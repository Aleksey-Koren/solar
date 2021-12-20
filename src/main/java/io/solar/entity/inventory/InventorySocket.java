package io.solar.entity.inventory;

import io.solar.entity.objects.ObjectTypeDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "object_type_socket")
public class InventorySocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ObjectTypeDescription item;

    @Column(name = "item_type_id")
    private Long itemTypeId;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "alias")
    private String alias;
}

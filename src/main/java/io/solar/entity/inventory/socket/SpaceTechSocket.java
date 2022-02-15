package io.solar.entity.inventory.socket;

import io.solar.entity.inventory.InventorySocket;
import io.solar.entity.objects.BasicObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "space_tech_sockets")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceTechSocket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_tech_id")
    @ToString.Exclude
    private BasicObject spaceTech;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socket_id")
    private InventorySocket inventorySocket;

    private Integer energyConsumptionPriority;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id")
    @ToString.Exclude
    private BasicObject object;

    public void attachItem(BasicObject item) {
        this.object = item;
        item.setAttachedToSocket(this.inventorySocket.getId());
    }

    public void detachItem() {
        this.object.setAttachedToSocket(null);
        this.object = null;
    }
}
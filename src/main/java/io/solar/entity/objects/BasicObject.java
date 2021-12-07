package io.solar.entity.objects;

import io.solar.entity.Planet;
import io.solar.entity.inventory.InventorySocket;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)
public class BasicObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne
    @JoinColumn(name = "planet")
    protected Planet planet;
    protected Long population;
    protected String fraction;
    protected String title;
    protected Float x;
    protected Float y;
    protected Float aphelion;
    protected Float orbitalPeriod;
    protected Float angle;
    @ManyToOne
    @JoinColumn(name = "hull_id")
    protected ObjectTypeDescription objectTypeDescription;
    protected Long userId;
    protected Boolean active;
    protected Long durability;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attached_to_ship")
    protected BasicObject attachedToShip;
    protected Long attachedToSocket;
    @Enumerated(EnumType.STRING)
    protected ObjectStatus status;
    protected Float acceleration;
    protected Float speed;

    @OneToMany(mappedBy = "attachedToShip", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @EqualsAndHashCode.Exclude
    protected List<BasicObject> attachedObjects;

    //TODO ??????
    private List<InventorySocket> socketList;

    @Override
    public String toString() {
        return "BasicObject{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
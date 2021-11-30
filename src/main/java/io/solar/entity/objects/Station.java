package io.solar.entity.objects;

import io.solar.entity.Production;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "stations")
public class Station extends BasicObject {

    @ManyToMany(cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "objects_productions",
               joinColumns = @JoinColumn(name = "object_id"),
               inverseJoinColumns = @JoinColumn(name = "production_id"))
    private List<Production> productions;


//    private List<Goods> goods;
//    private List<Inventory> inventory;
}
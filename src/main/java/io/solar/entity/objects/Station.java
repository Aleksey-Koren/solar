package io.solar.entity.objects;

import io.solar.entity.Production;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
@Table(name = "stations")
public class Station extends BasicObject {

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "station")
    private List<Production> production;

//    private List<Goods> goods;
//    private List<Inventory> inventory;
}
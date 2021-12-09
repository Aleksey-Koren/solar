package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.Production;
import io.solar.entity.interfaces.SpaceTech;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "stations")
public class Station extends BasicObject implements SpaceTech {


    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Production> production;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Goods> goods;

//    private List<Inventory> inventory;


    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
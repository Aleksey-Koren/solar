package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.Production;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "stations")
public class Station extends BasicObject {


    @OneToMany(mappedBy = "station", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @EqualsAndHashCode.Exclude
    private List<Production> production;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
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
package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.Production;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.inventory.socket.SpaceTechSocket;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.shop.StationShop;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "stations")
public class Station extends BasicObject implements SpaceTech {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Production> production;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    private List<Goods> goods;

    @OneToOne(mappedBy = "station", fetch = FetchType.LAZY)
    private StationShop shop;

    @OneToMany(mappedBy = "station")
    private List<ModificationPrice> modificationPrices;

    private Long money;

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
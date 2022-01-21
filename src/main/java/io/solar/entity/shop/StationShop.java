package io.solar.entity.shop;

import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.Station;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Data
@Table(name = "station_shops")
public class StationShop {

    @Id
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station")
    private Station station;
    private StationShopLevel shopLevel;

    @ManyToMany
    @JoinTable(name = "station_shops_otds",
            joinColumns = @JoinColumn(name = "shop_id"),
            inverseJoinColumns = @JoinColumn(name = "otd_id"))
    private List<ObjectTypeDescription> inventoryGoods;
}
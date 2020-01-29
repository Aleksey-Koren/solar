package io.solar.entity.objects;

import io.solar.entity.Goods;
import io.solar.entity.Inventory;
import io.solar.entity.Planet;
import io.solar.entity.Production;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Station extends AbstractObject {

    private List<Production> production;
    private List<Goods> goods;
    private List<Inventory> inventory;

}

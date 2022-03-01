package io.solar.entity.proxy;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;

import java.util.List;

public class SpaceTechProxy extends BasicObjectProxy implements SpaceTech {

    private SpaceTech spaceTech;

    public  SpaceTechProxy(StarShip spaceTech) {
        super(spaceTech);
        this.spaceTech = spaceTech;
    }

    public SpaceTechProxy(Station spaceTech) {
        super(spaceTech);
        this.spaceTech = spaceTech;
    }

    @Override
    public List<Goods> getGoods() {
        return spaceTech.getGoods();
    }

    @Override
    public User getUser() {
        return spaceTech.getUser();
    }
}
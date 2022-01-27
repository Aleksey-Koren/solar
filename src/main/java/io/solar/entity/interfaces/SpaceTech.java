package io.solar.entity.interfaces;

import io.solar.entity.Goods;
import io.solar.entity.User;

import java.util.List;

public interface SpaceTech {
    List<Goods> getGoods();
    User getUser();
}

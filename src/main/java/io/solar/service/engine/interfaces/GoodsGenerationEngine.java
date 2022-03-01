package io.solar.service.engine.interfaces;

import io.solar.entity.objects.Station;

public interface GoodsGenerationEngine {

    void generateGoods(Station station);

    void recalculatePrice(Station station);
}

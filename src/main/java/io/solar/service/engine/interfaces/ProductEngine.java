package io.solar.service.engine.interfaces;

import io.solar.dto.shop.ShopDto;
import io.solar.entity.interfaces.SpaceTech;

import java.util.List;

public interface ProductEngine {

    void transferProducts(SpaceTech from, SpaceTech to, List<ShopDto> products);

}

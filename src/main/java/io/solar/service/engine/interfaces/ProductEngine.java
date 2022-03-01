package io.solar.service.engine.interfaces;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.transfer.TransferProductsDto;
import io.solar.entity.Goods;
import io.solar.entity.interfaces.SpaceTech;

import java.util.List;
import java.util.Map;

public interface ProductEngine {

    void transferProducts(SpaceTech from, SpaceTech to, List<TransferProductsDto> products);

    Map<Long, Goods> createProductGoodsMap(SpaceTech spaceTech);

}

package io.solar.service.engine.modification;

import io.solar.entity.Goods;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.price.PriceProduct;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.engine.interfaces.modification.ModificationPriceEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ModificationPriceEngineImpl implements ModificationPriceEngine {

    private final ProductEngine productEngine;

    @Override
    public boolean isEnoughResources(SpaceTech spaceTech, ModificationPrice modificationPrice) {
        List<PriceProduct> priceProducts = modificationPrice.getPrice().getPriceProducts();
        Map<Long, Goods> goods = productEngine.createProductGoodsMap(spaceTech);
        return  priceProducts
                .stream()
                .noneMatch(s -> goods.get(s.getProduct().getId()).getAmount() < s.getProductAmount());
    }
}

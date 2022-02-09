package io.solar.service.engine;

import io.solar.dto.shop.ShopDto;
import io.solar.entity.Goods;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.repository.GoodsRepository;
import io.solar.service.ProductService;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductEngineImpl implements ProductEngine {

    private final ProductService productService;
    private final BasicObjectService basicObjectService;

    @Override
    public void transferProducts(SpaceTech from, SpaceTech to, List<ShopDto> products) {
        Map<Long, Goods> fromProductsGoods = createProductGoodsMap(from);
        Map<Long, Goods> toProductsGoods = createProductGoodsMap(to);

        if (isProductsInStock(products, fromProductsGoods)) {

            products.forEach(product -> {
                Goods goodsToDecrease = fromProductsGoods.get(product.getProductId());
                decreaseGoods(product, goodsToDecrease, from);

                Goods goodsToIncrease = toProductsGoods.get(product.getProductId());
                increaseGoods(product, goodsToIncrease, to);
            });

            basicObjectService.save((BasicObject) from);
            basicObjectService.save((BasicObject) to);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough goods");
        }
    }

    private void increaseGoods(ShopDto product, Goods goods, SpaceTech spaceTech) {
        if (goods != null) {
            goods.setAmount(goods.getAmount() + product.getQuantity());
        } else {
            goods = Goods.builder()
                    .product(productService.getById(product.getProductId()))
                    .owner((BasicObject) spaceTech)
                    .amount(product.getQuantity().longValue())
                    .build();

            spaceTech.getGoods().add(goods);
        }
    }

    private void decreaseGoods(ShopDto product, Goods goods, SpaceTech spaceTech) {
        goods.setAmount(goods.getAmount() - product.getQuantity());

        if (goods.getAmount() == 0) {
            spaceTech.getGoods().remove(goods);
        }
    }

    public Map<Long, Goods> createProductGoodsMap(SpaceTech spaceTech) {

        return spaceTech.getGoods()
                .stream()
                .collect(Collectors.toMap(goods -> goods.getProduct().getId(), Function.identity()));
    }

    private boolean isProductsInStock(List<ShopDto> products, Map<Long, Goods> productGoodsMap) {
        boolean isProductsInStock = true;

        for (ShopDto product : products) {
            Goods goods = productGoodsMap.get(product.getProductId());
            if (goods == null || goods.getAmount() < product.getQuantity()) {
                isProductsInStock = false;
            }
        }

        return isProductsInStock;
    }
}

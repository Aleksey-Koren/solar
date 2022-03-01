package io.solar.service.engine;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.transfer.TransferProductsDto;
import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.multithreading.StationMonitor;
import io.solar.service.GoodsService;
import io.solar.service.ProductService;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductEngineImpl implements ProductEngine {

    private final ProductService productService;
    private final GoodsService goodsService;
    private final BasicObjectService basicObjectService;
    private final SpaceTechEngine spaceTechEngine;
    private final StationMonitor stationMonitor;

    @Override
    public void transferProducts(SpaceTech from, SpaceTech to, List<TransferProductsDto> products) {
        Map<Long, TransferProductsDto> productsToTransfer = products.stream()
                .collect(Collectors.toMap(TransferProductsDto::getProductId, Function.identity()));

        Map<Long, Goods> fromProductsGoods = createProductGoodsMap(from);
        Map<Long, Goods> toProductsGoods = createProductGoodsMap(to);

        synchronized (stationMonitor.getMonitor(to.getId())) {

            if (isProductsInStock(products, fromProductsGoods) && isSpaceTechHaveEnoughSpace(to, productsToTransfer)) {

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
    }

    @Override
    public void addProductToSpaceTech(StarShip starship, Product product, Long productAmount) {
        Optional<Goods> goodsOptional = goodsService.findByOwnerAndProduct(starship, product);
        Goods goods;

        if (goodsOptional.isPresent()) {
            goods = goodsOptional.get();
            goods.setAmount(goods.getAmount() + productAmount);
        } else {
            goods = Goods.builder()
                    .product(product)
                    .amount(productAmount)
                    .owner(starship)
                    .build();
        }

        goodsService.save(goods);
    }

    @Override
    public Map<Long, Goods> createProductGoodsMap(SpaceTech spaceTech) {

        return spaceTech.getGoods()
                .stream()
                .collect(Collectors.toMap(goods -> goods.getProduct().getId(), Function.identity()));
    }


    private void increaseGoods(TransferProductsDto product, Goods goods, SpaceTech spaceTech) {
        if (goods != null) {
            goods.setAmount(goods.getAmount() + product.getProductAmount());
        } else {
            goods = Goods.builder()
                    .product(productService.getById(product.getProductId()))
                    .owner((BasicObject) spaceTech)
                    .amount(product.getProductAmount().longValue())
                    .build();

            spaceTech.getGoods().add(goods);
        }
    }

    private void decreaseGoods(TransferProductsDto product, Goods goods, SpaceTech spaceTech) {
        goods.setAmount(goods.getAmount() - product.getProductAmount());

        if (goods.getAmount() == 0) {
            spaceTech.getGoods().remove(goods);
        }
    }

    private boolean isSpaceTechHaveEnoughSpace(SpaceTech spaceTech, Map<Long, TransferProductsDto> products) {
        float spaceTechVolume = spaceTechEngine.calculateTotalVolume(spaceTech);
        float usedVolume = spaceTechEngine.calculateUsedVolume(spaceTech);

        double productsVolume = products.values().stream()
                .map(product -> productService.getById(product.getProductId()))
                .mapToDouble(s -> s.getBulk() * products.get(s.getId()).getProductAmount())
                .sum();

        return ((usedVolume + productsVolume) <= spaceTechVolume);
    }

    private boolean isProductsInStock(List<TransferProductsDto> products, Map<Long, Goods> productGoodsMap) {
        boolean isProductsInStock = true;

        for (TransferProductsDto product : products) {
            Goods goods = productGoodsMap.get(product.getProductId());
            if (goods == null || goods.getAmount() < product.getProductAmount()) {
                isProductsInStock = false;
                break;
            }
        }

        return isProductsInStock;
    }
}

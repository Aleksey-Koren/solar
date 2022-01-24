package io.solar.facade.shop;

import io.solar.dto.shop.ProductPriceDto;
import io.solar.dto.shop.ShopDto;
import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.interfaces.SpaceTech;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.facade.UserFacade;
import io.solar.service.ProductService;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.engine.interfaces.ProductEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductShopFacade {

    private final ProductService productService;
    private final StationService stationService;
    private final StarShipService starshipService;
    private final UserFacade userFacade;
    private final ProductEngine productEngine;

    public void buyProducts(User user, List<ShopDto> products) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        userFacade.decreaseUserBalance(user, calculatePurchasePrice(products));

        productEngine.transferProducts(station, spaceship, products);
    }

    public void sellProducts(User user, List<ShopDto> products) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        productEngine.transferProducts(spaceship, station, products);

        userFacade.increaseUserBalance(user, calculateTotalSellPrice(station, products));
    }

    public List<ProductPriceDto> getProductsSellPrices(User user, List<Long> productsIds) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        List<Long> stationProductIds = getStationGoodsIds(station);

        return productsIds.stream()
                .map(productId -> new ProductPriceDto(productId, calculateProductSellPrice(stationProductIds, productId)))
                .toList();
    }

    private long calculateTotalSellPrice(Station station, List<ShopDto> products) {
        List<Long> stationProductsIds = getStationGoodsIds(station);

        return products.stream()
                .map(product -> calculateProductSellPrice(stationProductsIds, product.getProductId()))
                .mapToLong(Long::longValue)
                .sum();
    }

    private long calculateProductSellPrice(List<Long> stationProductsIds, Long productId) {

        return (long) (stationProductsIds.contains(productId)
                ? Math.floor(productService.getById(productId).getPrice() * 0.5)
                : Math.floor(productService.getById(productId).getPrice() * 0.7));
    }

    private long calculatePurchasePrice(List<ShopDto> products) {

        return products.stream()
                .map(product -> productService.getById(product.getProductId()).getPrice() * product.getQuantity())
                .mapToLong(Long::longValue)
                .sum();
    }

    private List<Long> getStationGoodsIds(Station station) {

        return station.getGoods()
                .stream()
                .map(goods -> goods.getProduct().getId())
                .toList();
    }

}
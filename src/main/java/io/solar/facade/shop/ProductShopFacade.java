package io.solar.facade.shop;

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

        userFacade.increaseUserBalance(user, calculateSellPrice(station, products));
    }

    private long calculateSellPrice(Station station, List<ShopDto> products) {
        long price = 0L;
        List<Long> stationProductsIds = station.getGoods()
                .stream()
                .map(goods -> goods.getProduct().getId())
                .toList();

        for (ShopDto product : products) {
            price += stationProductsIds.contains(product.getProductId())
                    ? Math.floor(productService.getById(product.getProductId()).getPrice() * 0.5)
                    : Math.floor(productService.getById(product.getProductId()).getPrice() * 0.7);
        }

        return price;
    }

    private long calculatePurchasePrice(List<ShopDto> products) {

        return products.stream()
                .map(product -> productService.getById(product.getProductId()).getPrice() * product.getQuantity())
                .mapToLong(Long::longValue)
                .sum();
    }


}
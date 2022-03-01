package io.solar.facade.shop;

import io.solar.dto.shop.ProductPriceDto;
import io.solar.dto.shop.ShopDto;
import io.solar.dto.transfer.TransferProductsDto;
import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.service.ProductService;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ProductEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductShopFacade {

    private final ProductService productService;
    private final StationService stationService;
    private final StarShipService starshipService;
    private final UserService userService;
    private final ProductEngine productEngine;

    public void buyProducts(User user, List<ShopDto> dto) {
        List<TransferProductsDto> products = dto
                .stream()
                .map(s -> TransferProductsDto.builder()
                        .productId(s.getProductId())
                        .productAmount(s.getQuantity()).build())
                .toList();

        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        long purchasePrice = calculatePurchasePrice(products);

        productEngine.transferProducts(station, spaceship, products);

        userService.decreaseUserBalance(user, purchasePrice);

        if (station.getUser() != null) {
            userService.increaseUserBalance(station.getUser(), purchasePrice);
        }
    }

    public void sellProducts(User user, List<ShopDto> dto) {
        List<TransferProductsDto> products = dto
                .stream()
                .map(s -> TransferProductsDto.builder()
                        .productId(s.getProductId())
                        .productAmount(s.getQuantity()).build())
                .toList();

        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        productEngine.transferProducts(spaceship, station, products);

        userService.increaseUserBalance(user, calculateTotalSellPrice(station, products));
    }

    public List<ProductPriceDto> getProductsSellPrices(User user, List<Long> productsIds) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        List<Long> stationProductIds = getStationGoodsIds(station);

        return productsIds.stream()
                .map(productId -> new ProductPriceDto(productId, calculateProductSellPrice(stationProductIds, productId)))
                .toList();
    }

    private long calculateTotalSellPrice(Station station, List<TransferProductsDto> products) {
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

    private long calculatePurchasePrice(List<TransferProductsDto> products) {

        return products.stream()
                .map(product -> productService.getById(product.getProductId()).getPrice() * product.getProductAmount())
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
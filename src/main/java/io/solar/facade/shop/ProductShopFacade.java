package io.solar.facade.shop;

import io.solar.dto.shop.ProductPriceDto;
import io.solar.dto.shop.ShopDto;
import io.solar.dto.transfer.TransferProductsDto;
import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.service.GoodsService;
import io.solar.service.ProductService;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ProductEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductShopFacade {

    private final ProductService productService;
    private final GoodsService goodsService;
    private final StationService stationService;
    private final StarShipService starshipService;
    private final UserService userService;
    private final ProductEngine productEngine;

    public void buyProducts(User user, List<ShopDto> dto) {
        List<TransferProductsDto> products = dto
                .stream()
                .map(s -> TransferProductsDto.builder()
                        .productId(s.getProductId())
                        .productAmount(s.getQuantity())
                        .price(s.getPrice())
                        .build())
                .toList();

        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        if (!productEngine.isProductsPriceActual(products, station)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shop prices have been updated");
        }

        long purchasePrice = calculatePurchasePrice(products, station);

        productEngine.transferProducts(station, spaceship, products);

        userService.decreaseUserBalance(user, purchasePrice);

        if (station.getUser() != null) {
            userService.increaseUserBalance(station.getUser(), purchasePrice);
        }
    }

    public void sellProducts(User user, List<ShopDto> dto) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());
        StarShip spaceship = starshipService.getById(user.getLocation().getId());

        List<TransferProductsDto> products = dto
                .stream()
                .map(s -> TransferProductsDto.builder()
                        .productId(s.getProductId())
                        .productAmount(s.getQuantity()).build())
                .toList();

        if (!productEngine.isProductsAreBought(products, station)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not all products are bought");
        }

        productEngine.transferProducts(spaceship, station, products);
        userService.increaseUserBalance(user, calculateTotalSellPrice(station, products));

    }

    public List<ProductPriceDto> getProductsSellPrices(User user, List<Long> productsIds) {
        Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());

        return productsIds.stream()
                .map(productId -> new ProductPriceDto(productId, calculateProductSellPrice(station, productId)))
                .toList();
    }

    private long calculateTotalSellPrice(Station station, List<TransferProductsDto> products) {

        return products.stream()
                .map(product -> calculateProductSellPrice(station, product.getProductId()) * product.getProductAmount())
                .mapToLong(Long::longValue)
                .sum();
    }

    /**
     * return -1 if the station does not buy this product
     */
    private long calculateProductSellPrice(Station station, Long productId) {
        Optional<Goods> goodsOptional = goodsService.findByOwnerAndProductId(station, productId);

        if (goodsOptional.isPresent()) {
            Goods goods = goodsOptional.get();

            return goods.getIsAvailableForBuy()
                    ? goods.getBuyPrice()
                    : -1;
        }
        return productService.getById(productId).getPrice();
    }

    private long calculatePurchasePrice(List<TransferProductsDto> products, Station station) {

        return products.stream()
                .map(product -> goodsService.getByOwnerAndProductId(station, product.getProductId()).getSellPrice() * product.getProductAmount())
                .mapToLong(Long::longValue)
                .sum();
    }
}
package io.solar.controller.shop;

import io.solar.dto.shop.ProductPriceDto;
import io.solar.dto.shop.ShopDto;
import io.solar.dto.shop.StationShopDto;
import io.solar.entity.User;
import io.solar.facade.shop.InventoryShopFacade;
import io.solar.facade.shop.ProductShopFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/shop")
@RequiredArgsConstructor
public class StationShopController {

    private final InventoryShopFacade inventoryShopFacade;
    private final ProductShopFacade productShopFacade;
    private final UserService userService;

    @GetMapping("/{stationId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<StationShopDto> getShop(@PathVariable Long stationId) {

        return ResponseEntity.ok(inventoryShopFacade.getShopByStationId(stationId));
    }

    @PatchMapping("/buy/inventory")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> buyInventoryGoods(Principal principal, @RequestBody List<ShopDto> dto) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(inventoryShopFacade.buyInventory(user, dto)).build();
    }

    @PatchMapping("/sell/inventory")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> sellInventoryGoods(Principal principal, @RequestBody ShopDto dto) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(inventoryShopFacade.sellInventory(user, dto)).build();
    }

    @GetMapping("/sell/inventory/price")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Long> calculateInventorySellPrice(Principal principal, @RequestBody Long objectId) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.ok(inventoryShopFacade.getSellPrice(user, objectId));
    }

    @PostMapping("/buy/products")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void buyProductGoods(@RequestBody List<ShopDto> products, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        productShopFacade.buyProducts(user, products);
    }

    @PatchMapping("/sell/products")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public void sellProductGoods(@RequestBody List<ShopDto> products, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        productShopFacade.sellProducts(user, products);
    }

    @GetMapping("/sell/products/price")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<ProductPriceDto> calculateProductsSellPrice(@RequestBody List<Long> productsIds, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return productShopFacade.getProductsSellPrices(user, productsIds);
    }
}

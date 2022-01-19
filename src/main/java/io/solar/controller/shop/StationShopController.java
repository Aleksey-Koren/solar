package io.solar.controller.shop;

import io.solar.dto.shop.ShopDto;
import io.solar.dto.shop.StationShopDto;
import io.solar.entity.User;
import io.solar.facade.shop.StationShopFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/shop/station")
@RequiredArgsConstructor
public class StationShopController {

    private final StationShopFacade stationShopFacade;
    private final UserService userService;

    @GetMapping("/{stationId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<StationShopDto> getShop(@PathVariable Long stationId) {

        return ResponseEntity.ok(stationShopFacade.getShopByStationId(stationId));
    }

    @PatchMapping("/buy-inventory")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> byInventoryGoods (Principal principal, @RequestBody List<ShopDto> dto ) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(stationShopFacade.buyInventory(user, dto)).build();
    }

}

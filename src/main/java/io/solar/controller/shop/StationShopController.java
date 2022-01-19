package io.solar.controller.shop;

import io.solar.dto.shop.StationShopDto;
import io.solar.facade.shop.StationShopFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shop/station")
@RequiredArgsConstructor
public class StationShopController {

    private final StationShopFacade stationShopFacade;

    @GetMapping("/{stationId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<StationShopDto> getShop(@PathVariable Long stationId) {

        return ResponseEntity.ok(stationShopFacade.getShopByStationId(stationId));
    }

}

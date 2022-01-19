package io.solar.controller.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/station/shop")
@RequiredArgsConstructor
public class StationShopController {

    @PatchMapping("buy-inventory")
    public ResponseEntity<Void> buyInventoryGoods() {
        return null;
    }
}

package io.solar.controller.price;

import io.solar.dto.price.PriceDto;
import io.solar.entity.User;
import io.solar.facade.price.PriceFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceController {

    private final PriceFacade priceFacade;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<PriceDto> createPrice(@RequestBody PriceDto priceDto, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.ok(priceFacade.createPrice(priceDto, user));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<PriceDto> updatePrice(@RequestBody PriceDto priceDto, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.ok(priceFacade.updatePrice(priceDto, user));
    }

}

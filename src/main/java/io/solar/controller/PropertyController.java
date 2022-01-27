package io.solar.controller;

import io.solar.config.properties.AppProperties;
import io.solar.config.properties.MarketplaceProperties;
import io.solar.config.properties.MessengerProperties;
import io.solar.config.properties.StarShipProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/property")
@RequiredArgsConstructor
public class PropertyController {

    private final AppProperties appProperties;
    private final MarketplaceProperties marketplaceProperties;
    private final MessengerProperties messengerProperties;
    private final StarShipProperties starShipProperties;

    @GetMapping("/time-flow")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public ResponseEntity<Integer> getTimeFlowModifier() {

        return ResponseEntity.ok(appProperties.getTimeFlowModifier());
    }

}

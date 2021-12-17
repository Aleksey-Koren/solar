package io.solar.controller;

import io.solar.service.NavigatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/navigate")
public class NavigatorController {

    private final NavigatorService navigatorService;

    @PostMapping("/dock")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @ResponseStatus
    public void dockShip(@RequestParam Long stationId,
                         @RequestParam Long shipId) {

        navigatorService.dockShip(stationId, shipId);
    }

    @PostMapping("/undock")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    public void undockShip(@RequestParam Long shipId) {

        navigatorService.undockShip(shipId);
    }
}

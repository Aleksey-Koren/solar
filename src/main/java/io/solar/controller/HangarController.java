package io.solar.controller;

import io.solar.dto.object.StarShipDto;
import io.solar.entity.User;
import io.solar.facade.HangarFacade;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/station/hangar")
@RequiredArgsConstructor
public class HangarController {

    private final UserService userService;
    private final HangarFacade hangarFacade;

    @GetMapping("/{stationId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public List<StarShipDto> getStarshipsInHangar(@PathVariable Long stationId, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return hangarFacade.getAllStarships(stationId, user);
    }

    @PatchMapping("/starship/{starshipId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> boardStarShip(@PathVariable Long starshipId,
                                              Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(hangarFacade.boardStarShip(starshipId, user)).build();
    }
}
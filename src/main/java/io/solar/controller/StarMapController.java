package io.solar.controller;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.facade.StarMapFacade;
import io.solar.service.StarShipService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/star_map")
public class StarMapController {

    private final UserService userService;
    private final StarMapFacade starMapFacade;
    private final StarShipService starShipService;

    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @GetMapping("/user/view")
    @Transactional
    public List<BasicObjectViewDto> getUsersView(Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Optional<StarShip> starShip = starShipService.findById(user.getLocation().getId());
        if(!starShip.isPresent()) {
            return new ArrayList<>();
        }
        return starMapFacade.getStarshipView(starShip.get());
    }

    @PatchMapping("/object/{objectId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> pickUpObject(@PathVariable Long objectId, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.status(starMapFacade.pickUpObject(user, objectId)).build();
    }
}
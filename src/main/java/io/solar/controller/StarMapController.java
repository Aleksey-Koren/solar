package io.solar.controller;

import io.solar.dto.BasicObjectViewDto;
import io.solar.entity.User;
import io.solar.entity.objects.StarShip;
import io.solar.facade.StarMapFacade;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class StarMapController {

    private final UserService userService;
    private final StarMapFacade starMapFacade;
    private final StarShipService starShipService;

    public List<BasicObjectViewDto> getUsersView(Principal principal) {
        User user = userService.findByLogin(principal.getName());
        Optional<StarShip> starShip = starShipService.findById(user.getLocation().getId());
        if(!starShip.isPresent()) {
            return new ArrayList<>();
        }
        return starMapFacade.getStarshipView(starShip.get());
    }
}
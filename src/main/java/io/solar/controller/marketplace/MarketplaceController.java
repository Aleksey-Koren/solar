package io.solar.controller.marketplace;

import io.solar.dto.marketplace.MarketplaceBetDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.User;
import io.solar.facade.marketplace.MarketplaceFacade;
import io.solar.service.UserService;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.security.Principal;

@RestController
@RequestMapping("api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final UserService userService;
    private final MarketplaceFacade marketplaceFacade;

    @GetMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MarketplaceLotDto> getMarketplaceLots(MarketplaceLotFilter filter, Principal principal, Pageable pageable) {
        User user = userService.findByLogin(principal.getName());

        return null;
    }

    @PostMapping("/lot")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> createLot(MarketplaceLotDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceFacade.createLot(dto, user)).build();
    }

    @PostMapping("/lot/bet")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> makeBet(@RequestBody MarketplaceBetDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceFacade.makeBet(dto, user)).build();
    }
}

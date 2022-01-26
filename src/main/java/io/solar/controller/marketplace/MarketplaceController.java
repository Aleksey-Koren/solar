package io.solar.controller.marketplace;

import io.solar.dto.marketplace.MarketplaceBetDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.User;
import io.solar.facade.marketplace.MarketplaceBetFacade;
import io.solar.service.UserService;
import io.solar.facade.marketplace.MarketplaceLotFacade;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceLotFacade marketplaceLotFacade;
    private final UserService userService;
    private final MarketplaceBetFacade marketplaceBetFacade;

    @GetMapping("/lot")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MarketplaceLotDto> getMarketplaceLots(MarketplaceLotFilter filter, Pageable pageable) {

        return marketplaceLotFacade.findAll(pageable, filter);
    }

    @PatchMapping("/lot/{lotId}")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> pickUpWonLot(@PathVariable Long lotId, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.status(marketplaceLotFacade.pickUpLot(user, lotId)).build();
    }

    @PatchMapping("/lot/{lotId}/money")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> takeMoney(@PathVariable Long lotId, Principal principal) {
        User user = userService.findByLogin(principal.getName());

        return ResponseEntity.status(marketplaceLotFacade.takeMoney(user, lotId)).build();
    }

    @PostMapping("/lot")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> createLot(MarketplaceLotDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceLotFacade.createLot(dto, user)).build();
    }

    @PostMapping("/lot/bet")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> makeBet(@RequestBody MarketplaceBetDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceBetFacade.makeBet(dto, user)).build();
    }

    @PostMapping("/lot/instant-purchase")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> instantPurchase(@RequestBody MarketplaceLotDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceLotFacade.instantPurchase(dto, user)).build();
    }

    @PostMapping("/lot/expired")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public ResponseEntity<Void> takeAwayExpiredLot(@RequestBody MarketplaceLotDto dto, Principal principal) {
        User user = userService.findByLogin(principal.getName());
        return ResponseEntity.status(marketplaceLotFacade.takeAwayExpiredLot(dto, user)).build();
    }
}
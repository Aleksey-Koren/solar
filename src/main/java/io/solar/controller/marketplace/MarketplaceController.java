package io.solar.controller.marketplace;

import io.solar.dto.marketplace.LotBetDto;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.User;
import io.solar.service.UserService;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;
import java.security.Principal;

@RestController
@RequestMapping("api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MarketplaceLotDto> getMarketplaceLots(MarketplaceLotFilter filter, Principal principal, Pageable pageable) {
        User user = userService.findByLogin(principal.getName());

        return null;
    }

}

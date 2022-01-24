package io.solar.controller.marketplace;

import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.facade.marketplace.MarketplaceLotFacade;
import io.solar.service.UserService;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceLotFacade marketplaceLotFacade;
    private final UserService userService;

    @GetMapping("/lot")
    @PreAuthorize("hasAuthority('PLAY_THE_GAME')")
    @Transactional
    public Page<MarketplaceLotDto> getMarketplaceLots(MarketplaceLotFilter filter, Pageable pageable) {

        return marketplaceLotFacade.findAll(pageable, filter);
    }

}

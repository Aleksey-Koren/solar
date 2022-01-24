package io.solar.facade.marketplace;

import io.solar.service.marketplace.MarketplaceBetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketplaceBetFacade {

    private final MarketplaceBetService marketplaceBetService;

}

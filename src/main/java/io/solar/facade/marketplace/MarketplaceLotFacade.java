package io.solar.facade.marketplace;

import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.mapper.marketplace.MarketplaceLotMapper;
import io.solar.service.marketplace.MarketplaceLotService;
import io.solar.specification.MarketplaceLotSpecification;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketplaceLotFacade {

    private final MarketplaceLotService marketplaceLotService;
    private final MarketplaceLotMapper marketplaceLotMapper;

    public Page<MarketplaceLotDto> findAll(Pageable pageable, MarketplaceLotFilter filter) {

        return marketplaceLotService.findAll(pageable, new MarketplaceLotSpecification(filter))
                .map(marketplaceLotMapper::toDto);
    }

}

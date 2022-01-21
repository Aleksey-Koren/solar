package io.solar.mapper.marketplace;

import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.mapper.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarketplaceLotMapper implements EntityDtoMapper<MarketplaceLot, MarketplaceLotDto> {

    @Override
    public MarketplaceLot toEntity(MarketplaceLotDto dto) {

        return null;
    }

    @Override
    public MarketplaceLotDto toDto(MarketplaceLot entity) {

        return MarketplaceLotDto.builder()
                .id(entity.getId())
                .objectId(entity.getObject().getId())
                .ownerId(entity.getOwner().getId())
                .startDate(entity.getStartDate())
                .finishDate(entity.getFinishDate())
                .startPrice(entity.getStartPrice())
                .instantPrice(entity.getInstantPrice())
                .isBuyerHasTaken(entity.getIsBuyerHasTaken())
                .isSellerHasTaken(entity.getIsSellerHasTaken())
                .build();
    }
}

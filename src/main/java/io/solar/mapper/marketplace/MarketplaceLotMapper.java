package io.solar.mapper.marketplace;

import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.UserService;
import io.solar.service.marketplace.MarketplaceBetService;
import io.solar.service.marketplace.MarketplaceLotService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarketplaceLotMapper implements EntityDtoMapper<MarketplaceLot, MarketplaceLotDto> {

    private final BasicObjectService basicObjectService;
    private final MarketplaceBetMapper marketplaceBetMapper;
    private final MarketplaceLotService marketplaceLotService;


    @Override
    public MarketplaceLot toEntity(MarketplaceLotDto dto) {
        return dto.getId() == null
                ? createLot(dto)
                : updateLot(dto);
    }


    private MarketplaceLot createLot(MarketplaceLotDto dto) {

        return MarketplaceLot.builder()
                .object(basicObjectService.getById(dto.getId()))
                .startPrice(dto.getStartPrice())
                .instantPrice(dto.getInstantPrice())
                .build();
    }

    private MarketplaceLot updateLot(MarketplaceLotDto dto) {
        return null;
    }

    @Override
    public MarketplaceLotDto toDto(MarketplaceLot entity) {

        Optional<MarketplaceBet> currentBet = marketplaceLotService.findCurrentBet(entity);

        return MarketplaceLotDto.builder()
                .id(entity.getId())
                .objectId(entity.getObject().getId())
                .ownerId(entity.getOwner().getId())
                .currentBet(currentBet.map(marketplaceBetMapper::toDto).orElse(null))
                .startDate(entity.getStartDate())
                .finishDate(entity.getFinishDate())
                .startPrice(entity.getStartPrice())
                .instantPrice(entity.getInstantPrice())
                .isBuyerHasTaken(entity.getIsBuyerHasTaken())
                .isSellerHasTaken(entity.getIsSellerHasTaken())
                .build();
    }
}

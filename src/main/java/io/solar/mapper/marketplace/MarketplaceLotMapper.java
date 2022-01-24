package io.solar.mapper.marketplace;

import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.objects.BasicObject;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.UserService;
import io.solar.service.object.BasicObjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MarketplaceLotMapper implements EntityDtoMapper<MarketplaceLot, MarketplaceLotDto> {

    private final BasicObjectService basicObjectService;
    private final UserService userService;


    @Override
    public MarketplaceLot toEntity(MarketplaceLotDto dto) {
        return null;
    }

    private MarketplaceLot createLot(MarketplaceLotDto dto) {

        return MarketplaceLot.builder()
                .object(basicObjectService.getById(dto.getId()))
                .owner(userService.getById(dto.getId()))
                .startDate(Instant.now())
                .build();

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

package io.solar.mapper.marketplace;

import io.solar.dto.marketplace.MarketplaceBetDto;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.UserService;
import io.solar.service.marketplace.MarketplaceBetService;
import io.solar.service.marketplace.MarketplaceLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class MarketplaceBetMapper implements EntityDtoMapper<MarketplaceBet, MarketplaceBetDto> {

    private final MarketplaceLotService marketplaceLotService;
    private final MarketplaceBetService marketplaceBetService;
    private final UserService userService;


    @Override
    public MarketplaceBet toEntity(MarketplaceBetDto dto) {

        return dto.getLotId() == null
                ? createLotBet(dto)
                : updateLotBet(dto);
    }

    @Override
    public MarketplaceBetDto toDto(MarketplaceBet entity) {

        return MarketplaceBetDto.builder()
                .lotId(entity.getId())
                .amount(entity.getAmount())
                .userId(entity.getUser().getId())
                .betTime(entity.getBetTime())
                .build();
    }

    private MarketplaceBet createLotBet(MarketplaceBetDto betDto) {

        return MarketplaceBet.builder()
                .lot(marketplaceLotService.getById(betDto.getLotId()))
                .user(userService.getById(betDto.getUserId()))
                .amount(betDto.getAmount())
                .build();
    }

    private MarketplaceBet updateLotBet(MarketplaceBetDto betDto) {
        MarketplaceBet marketplaceBet = marketplaceBetService.getById(betDto.getLotId());

        marketplaceBet.setUser(userService.getById(betDto.getUserId()));
        marketplaceBet.setBetTime(Instant.now());
        marketplaceBet.setAmount(betDto.getAmount());

        return marketplaceBet;
    }
}

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

        return dto.getId() == null
                ? createBet(dto)
                : updateBet(dto);
    }

    @Override
    public MarketplaceBetDto toDto(MarketplaceBet entity) {

        return MarketplaceBetDto.builder()
                .id(entity.getId())
                .lotId(entity.getId())
                .userId(entity.getUser().getId())
                .amount(entity.getAmount())
                .betTime(entity.getBetTime())
                .build();
    }

    private MarketplaceBet createBet(MarketplaceBetDto betDto) {

        return MarketplaceBet.builder()
                .lot(marketplaceLotService.getById(betDto.getLotId()))
                .amount(betDto.getAmount())
                .build();
    }

    private MarketplaceBet updateBet(MarketplaceBetDto betDto) {
        MarketplaceBet marketplaceBet = marketplaceBetService.getById(betDto.getLotId());

        marketplaceBet.setUser(userService.getById(betDto.getUserId()));
        marketplaceBet.setBetTime(Instant.now());
        marketplaceBet.setAmount(betDto.getAmount());

        return marketplaceBet;
    }
}
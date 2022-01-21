package io.solar.mapper.marketplace;

import io.solar.dto.marketplace.LotBetDto;
import io.solar.entity.marketplace.LotBet;
import io.solar.mapper.EntityDtoMapper;
import io.solar.service.UserService;
import io.solar.service.marketplace.LotBetService;
import io.solar.service.marketplace.MarketplaceLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LotBetMapper implements EntityDtoMapper<LotBet, LotBetDto> {

    private final MarketplaceLotService marketplaceLotService;
    private final LotBetService lotBetService;
    private final UserService userService;


    @Override
    public LotBet toEntity(LotBetDto dto) {

        return dto.getLotId() == null
                ? createLotBet(dto)
                : updateLotBet(dto);
    }

    @Override
    public LotBetDto toDto(LotBet entity) {

        return LotBetDto.builder()
                .lotId(entity.getId())
                .amount(entity.getAmount())
                .userId(entity.getUser().getId())
                .build();
    }

    private LotBet createLotBet(LotBetDto lotBetDto) {

        return LotBet.builder()
                .lot(marketplaceLotService.getById(lotBetDto.getLotId()))
                .betTime(Instant.now())
                .user(userService.getById(lotBetDto.getUserId()))
                .amount(lotBetDto.getAmount())
                .build();
    }

    private LotBet updateLotBet(LotBetDto lotBetDto) {
        LotBet lotBet = lotBetService.getById(lotBetDto.getLotId());

        //todo: need check, because can change id
//        lotBet.setLot(marketplaceLotService.getById(lotBetDto.getLotId()));
        lotBet.setUser(userService.getById(lotBetDto.getUserId()));
        lotBet.setBetTime(Instant.now());
        lotBet.setAmount(lotBetDto.getAmount());

        return lotBetService.save(lotBet);
    }
}

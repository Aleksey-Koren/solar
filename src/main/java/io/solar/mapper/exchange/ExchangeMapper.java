package io.solar.mapper.exchange;

import io.solar.dto.exchange.ExchangeDto;
import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.exchange.Exchange;
import io.solar.mapper.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeMapper {

    private final ExchangeOfferMapper exchangeOfferMapper;

    public ExchangeDto toDto(Exchange entity) {
        return ExchangeDto.builder()
                .id(entity.getId())
                .firstUserId(entity.getFirstUser().getId())
                .secondUserId(entity.getSecondUser().getId())
                .firstAccepted(entity.getFirstAccepted())
                .secondAccepted(entity.getSecondAccepted())
                .build();
    }

    public ExchangeDto toDtoWithOffers(Exchange entity) {
        List<ExchangeOfferDto> offerDtoList = entity.getExchangeOffers()
                .stream()
                .map(exchangeOfferMapper::toDto)
                .toList();

        return ExchangeDto.builder()
                .id(entity.getId())
                .firstUserId(entity.getFirstUser().getId())
                .secondUserId(entity.getSecondUser().getId())
                .firstAccepted(entity.getFirstAccepted())
                .secondAccepted(entity.getSecondAccepted())
                .offers(offerDtoList)
                .build();
    }
}

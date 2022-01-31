package io.solar.mapper.exchange;

import io.solar.dto.exchange.ExchangeDto;
import io.solar.entity.exchange.Exchange;
import io.solar.mapper.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeMapper{

    public ExchangeDto toDto(Exchange entity) {
        return ExchangeDto.builder()
                .id(entity.getId())
                .firstUserId(entity.getFirstUser().getId())
                .secondUserId(entity.getSecondUser().getId())
                .firstAccepted(entity.getFirstAccepted())
                .secondAccepted(entity.getSecondAccepted())
                .build();
    }
}

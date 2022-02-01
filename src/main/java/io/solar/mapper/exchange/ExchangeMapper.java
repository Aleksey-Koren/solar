package io.solar.mapper.exchange;

import io.solar.dto.exchange.ExchangeDto;
import io.solar.entity.exchange.Exchange;
import io.solar.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeMapper {

    private final UserMapper userMapper;

    public ExchangeDto toDto(Exchange entity) {
        return ExchangeDto.builder()
                .id(entity.getId())
                .firstUser(userMapper.toDtoWithIdAndTitle(entity.getFirstUser()))
                .secondUser(userMapper.toDtoWithIdAndTitle(entity.getSecondUser()))
                .firstAccepted(entity.getFirstAccepted())
                .secondAccepted(entity.getSecondAccepted())
                .build();
    }
}

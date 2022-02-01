package io.solar.mapper.exchange;

import io.solar.dto.exchange.ExchangeOfferDto;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.mapper.ProductMapper;
import io.solar.mapper.UserMapper;
import io.solar.mapper.object.BasicObjectViewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExchangeOfferMapper {

    private final UserMapper userMapper;
    private final BasicObjectViewMapper basicObjectViewMapper;
    private final ProductMapper productMapper;

    public ExchangeOfferDto toDto(ExchangeOffer entity) {
        return ExchangeOfferDto.builder()
                .id(entity.getId())
                .exchangeId(entity.getExchange().getId())
                .user(userMapper.toDtoWithIdAndTitle(entity.getUser()))
                .inventoryObject(entity.getInventoryObject() != null
                        ? basicObjectViewMapper.toDto(entity.getInventoryObject())
                        : null)
                .moneyAmount(entity.getMoneyAmount())
                .product(productMapper.toDto(entity.getProduct()))
                .productAmount(entity.getProductAmount())
                .offerType(entity.getOfferType())
                .build();
    }
}

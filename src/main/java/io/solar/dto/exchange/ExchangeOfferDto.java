package io.solar.dto.exchange;

import io.solar.dto.object.BasicObjectViewDto;
import io.solar.dto.ProductDto;
import io.solar.dto.UserDto;
import io.solar.entity.exchange.OfferType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeOfferDto {

    private Long id;
    private Long exchangeId;
    private UserDto user;
    private BasicObjectViewDto inventoryObject;
    private Long moneyAmount;
    private ProductDto product;
    private Long productAmount;
    private OfferType offerType;
}
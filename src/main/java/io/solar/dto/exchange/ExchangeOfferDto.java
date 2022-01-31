package io.solar.dto.exchange;

import io.solar.dto.BasicObjectViewDto;
import io.solar.dto.ProductDto;
import io.solar.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeOfferDto {

    private Long id;
    private ExchangeDto exchange;
    private UserDto user;
    private BasicObjectViewDto inventoryObject;
    private Long moneyAmount;
    private ProductDto product;
    private Long productAmount;
    private String offerType;
}

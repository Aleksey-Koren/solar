package io.solar.dto.price;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PriceDto {
    private Long id;
    private Long moneyAmount;
    private List<PriceProductDto> priceProductDtoList;
}

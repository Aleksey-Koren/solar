package io.solar.dto.price;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceProductDto {
    private Long id;
    private Long priceId;
    private Long productId;
    private Integer amount;
}

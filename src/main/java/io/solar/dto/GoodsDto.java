package io.solar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoodsDto {
    private Long id;
    private Long station;
    private Long product;
    private Long amount;
    private Long buyPrice;
    private Long sellPrice;
    private Boolean isAvailableForSale;
    private Boolean isAvailableForBuy;
}
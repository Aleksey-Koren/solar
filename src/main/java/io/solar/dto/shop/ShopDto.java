package io.solar.dto.shop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopDto {
    private Long otdId;
    private Long productId;
    private Integer quantity;
}
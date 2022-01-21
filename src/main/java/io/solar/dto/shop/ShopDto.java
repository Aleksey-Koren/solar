package io.solar.dto.shop;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShopDto {
    private Long otdId;
    private Long productId;
    private Integer quantity;
    private List<Long> objectIds;
}
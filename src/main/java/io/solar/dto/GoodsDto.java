package io.solar.dto;

import lombok.Data;

@Data
public class GoodsDto {

    private Long station;
    private Long product;
    private Long amount;
    private Float price;
}
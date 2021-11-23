package io.solar.dto;

import lombok.Data;

@Data
public class ProductDto {

    private Long id;
    private String title;
    private String image;
    private Float bulk;
    private Float mass;
    private Float price;
}

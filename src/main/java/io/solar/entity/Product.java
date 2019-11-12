package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private Long id;
    private String title;
    private String image;
    private Float bulk;
    private Float mass;
    private Float price;
}

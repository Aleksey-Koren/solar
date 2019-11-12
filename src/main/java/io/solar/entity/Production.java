package io.solar.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Production {
    private Long id;
    private Long station;
    private Long product;
    private Float power;
}

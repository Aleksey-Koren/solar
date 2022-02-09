package io.solar.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StarshipPriceDto {
    private Long starshipId;
    private Long price;
}

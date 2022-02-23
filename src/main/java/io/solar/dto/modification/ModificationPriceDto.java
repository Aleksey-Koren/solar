package io.solar.dto.modification;

import io.solar.dto.price.PriceDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModificationPriceDto {
    private Long id;
    private Long modificationId;
    private PriceDto priceDto;
    private Long stationId;
}

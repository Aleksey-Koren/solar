package io.solar.dto.transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferProductsDto {
    private Long productId;
    private Integer productAmount;
    private Long price;
}

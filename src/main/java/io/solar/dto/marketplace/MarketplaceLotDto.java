package io.solar.dto.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketplaceLotDto {
    private Long id;
    private Long objectId;
    private Long ownerId;
    private Instant startDate;
    private Instant finishDate;
    private Long startPrice;
    private Long instantPrice;
    private Boolean isBuyerHasTaken;
    private Boolean isSellerHasTaken;
}

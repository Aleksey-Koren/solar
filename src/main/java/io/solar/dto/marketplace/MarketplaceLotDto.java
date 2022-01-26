package io.solar.dto.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarketplaceLotDto {
    private Long id;
    private Long objectId;
    private Long ownerId;
    private MarketplaceBetDto currentBet;
    private Instant startDate;
    private Instant finishDate;
    private Long durationSeconds;
    private Long startPrice;
    private Long instantPrice;
    private Boolean isBuyerHasTaken;
    private Boolean isSellerHasTaken;
}

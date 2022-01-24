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
public class MarketplaceBetDto {
    private Long id;
    private Long lotId;
    private Long userId;
    private Long amount;
    private Instant betTime;
}

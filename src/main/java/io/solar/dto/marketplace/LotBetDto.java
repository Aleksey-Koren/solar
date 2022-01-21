package io.solar.dto.marketplace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LotBetDto {
    private Long lotId;
    private Long userId;
    private Long amount;
}

package io.solar.dto.exchange;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeDto {

    private Long id;
    private Long firstUserId;
    private Long secondUserId;
    private Boolean firstAccepted;
    private Boolean secondAccepted;

}
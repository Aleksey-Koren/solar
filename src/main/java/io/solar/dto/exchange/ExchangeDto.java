package io.solar.dto.exchange;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExchangeDto {

    private Long id;
    private Long firstUserId;
    private Long secondUserId;
    private Boolean firstAccepted;
    private Boolean secondAccepted;

    private List<ExchangeOfferDto> offers;
}
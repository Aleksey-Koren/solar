package io.solar.dto.exchange;

import io.solar.dto.UserDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExchangeDto {

    private Long id;
    private UserDto firstUser;
    private UserDto secondUser;
    private Boolean firstAccepted;
    private Boolean secondAccepted;
    private Double distance;

    private List<ExchangeOfferDto> offers;

}
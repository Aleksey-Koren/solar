package io.solar.dto.exchange;

import io.solar.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class LayerTransferDto {

    private ExchangeOfferDto offerDto;
    private UserDto firstUser;
    private UserDto secondUser;
}

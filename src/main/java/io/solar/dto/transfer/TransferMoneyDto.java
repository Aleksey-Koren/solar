package io.solar.dto.transfer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferMoneyDto {

    private Long userMoney;
    private Long stationMoney;
}

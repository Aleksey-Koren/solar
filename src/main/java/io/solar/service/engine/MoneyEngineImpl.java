package io.solar.service.engine;

import io.solar.dto.transfer.TransferMoneyDto;
import io.solar.entity.User;
import io.solar.entity.objects.Station;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.MoneyEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoneyEngineImpl implements MoneyEngine {

    private final UserService userService;
    private final StationService stationService;

    @Override
    public TransferMoneyDto transferMoney(User user, Station station, Long amount) {
        return TransferMoneyDto.builder()
                .userMoney(userService.decreaseUserBalance(user, amount))
                .stationMoney(stationService.increaseBalance(station, amount))
                .build();
    }
}
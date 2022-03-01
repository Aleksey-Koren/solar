package io.solar.service.engine.interfaces;

import io.solar.dto.transfer.TransferMoneyDto;
import io.solar.entity.User;
import io.solar.entity.objects.Station;

public interface MoneyEngine {

    TransferMoneyDto transferMoney(User user, Station station, Long amount);
}

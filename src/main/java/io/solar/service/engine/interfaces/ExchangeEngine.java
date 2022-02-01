package io.solar.service.engine.interfaces;

import io.solar.entity.exchange.ExchangeOffer;

public interface ExchangeEngine {
    
    void putObject(ExchangeOffer offer);

    void returnObjectToUser(ExchangeOffer offer);

    void updateUserBalance(ExchangeOffer offer, Long updatedMoneyAmount);

    void updateGoods(ExchangeOffer offer, Long updatedGoodsAmount);

    void createMoneyOffer(ExchangeOffer exchangeOffer);

    void createGoodsOffer(ExchangeOffer exchangeOffer);
}

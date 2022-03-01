package io.solar.service.engine.interfaces;

import io.solar.entity.exchange.ExchangeOffer;

public interface ExchangeOfferEngine {

    void transferMoney(ExchangeOffer offer);

    void transferGoods(ExchangeOffer offer);

    void transferObject(ExchangeOffer offer);

    void createMoneyOffer(ExchangeOffer exchangeOffer);

    void createGoodsOffer(ExchangeOffer exchangeOffer);
}

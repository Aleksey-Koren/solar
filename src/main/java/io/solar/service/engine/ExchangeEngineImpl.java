package io.solar.service.engine;


import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.objects.StarShip;
import io.solar.service.GoodsService;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.exchange.ExchangeOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExchangeEngineImpl implements ExchangeEngine {

    private final StarShipService starShipService;
    private final ExchangeOfferService exchangeOfferService;
    private final GoodsService goodsService;
    private final UserService userService;
    private final InventoryEngine inventoryEngine;

    @Override
    public void returnObjectToUser(ExchangeOffer offer) {
        StarShip starship = starShipService.getById(offer.getUser().getLocation().getId());

        inventoryEngine.putToInventory(starship, List.of(offer.getInventoryObject()));
        exchangeOfferService.delete(offer);
    }

    @Override
    public void updateUserBalance(ExchangeOffer offer, Long updatedMoneyAmount) {
        long diffMoneyAmount = offer.getMoneyAmount() - updatedMoneyAmount;

        if (diffMoneyAmount > 0) {
            userService.increaseUserBalance(offer.getUser(), diffMoneyAmount);
        } else {
            userService.decreaseUserBalance(offer.getUser(), Math.abs(diffMoneyAmount));
        }
    }

    @Override
    //TODO: need refactor
    public void updateGoods(ExchangeOffer offer, Long updatedGoodsAmount) {
        Goods goods = goodsService.getByOwnerAndProduct(offer.getUser().getLocation(), offer.getProduct());

        long diffGoodsAmount = offer.getProductAmount() - updatedGoodsAmount;
        goods.setAmount(goods.getAmount() + diffGoodsAmount);

        if (updatedGoodsAmount == 0) {
            exchangeOfferService.delete(offer);
        }

        if (goods.getAmount() == 0) {
            goodsService.delete(goods);
        }

        goodsService.save(goods);
    }

}

package io.solar.service.engine;


import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.objects.StarShip;
import io.solar.service.GoodsService;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.exchange.ExchangeOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExchangeEngineImpl implements ExchangeEngine {

    private final StarShipService starShipService;
    private final ExchangeOfferService exchangeOfferService;
    private final GoodsService goodsService;
    private final UserService userService;
    private final InventoryEngine inventoryEngine;
    private final ProductEngine productEngine;

    @Override
    public void putObject(ExchangeOffer offer) {
        StarShip starship = starShipService.getById(offer.getUser().getLocation().getId());
        inventoryEngine.putToExchange(starship, offer.getInventoryObject());
    }


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

        if (updatedMoneyAmount == 0) {
            exchangeOfferService.delete(offer);
        }

    }

    @Override
    public void updateGoods(ExchangeOffer offer, Long updatedGoodsAmount) {
        Goods goods = goodsService.getByOwnerAndProduct(offer.getUser().getLocation(), offer.getProduct());

        long diffGoodsAmount = offer.getProductAmount() - updatedGoodsAmount;
        goods.setAmount(goods.getAmount() + diffGoodsAmount);

        if (updatedGoodsAmount == 0) {
            exchangeOfferService.delete(offer);
        }

        if (goods.getAmount() == 0) {
            goodsService.delete(goods);
        } else {
            goodsService.save(goods);
        }
    }

    @Override
    public User retrieveAnotherExchangeUser(User user, Exchange exchange) {

        return exchange.getFirstUser().equals(user)
                ? exchange.getSecondUser()
                : exchange.getFirstUser();
    }

    @Override
    //TODO add check. If any money offers were created before?
    public void createMoneyOffer(ExchangeOffer exchangeOffer) {
        userService.decreaseUserBalance(exchangeOffer.getUser(), exchangeOffer.getMoneyAmount());
    }

    @Override
    public void createGoodsOffer(ExchangeOffer exchangeOffer) {
        StarShip starship = starShipService.getById(exchangeOffer.getUser().getLocation().getId());
        Long productId = exchangeOffer.getProduct().getId();
        Map<Long, Goods> productGoodsMap = productEngine.createProductGoodsMap(starship);
        Goods goodsAtUsers = productGoodsMap.get(productId);

        if (goodsAtUsers.getAmount() < exchangeOffer.getProductAmount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("There is no enough %s product at user's starship", exchangeOffer.getProduct().getTitle()));
        }

        goodsAtUsers.setAmount(goodsAtUsers.getAmount() - exchangeOffer.getProductAmount());
        goodsService.save(goodsAtUsers);
    }
}

package io.solar.service.engine;

import io.solar.entity.Goods;
import io.solar.entity.User;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.objects.StarShip;
import io.solar.service.GoodsService;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.ExchangeEngine;
import io.solar.service.engine.interfaces.ExchangeOfferEngine;
import io.solar.service.engine.interfaces.ProductEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExchangeOfferEngineImpl implements ExchangeOfferEngine {

    private final ExchangeEngine exchangeEngine;
    private final InventoryEngine inventoryEngine;
    private final SpaceTechEngine spaceTechEngine;
    private final ProductEngine productEngine;
    private final UserService userService;
    private final StarShipService starShipService;
    private final GoodsService goodsService;

    @Override
    public void transferMoney(ExchangeOffer offer) {
        Long moneyAmount = offer.getMoneyAmount();
        User user = exchangeEngine.retrieveAnotherExchangeUser(offer.getUser(), offer.getExchange());

        userService.increaseUserBalance(user, moneyAmount);
    }

    @Override
    public void transferGoods(ExchangeOffer offer) {
        User user = exchangeEngine.retrieveAnotherExchangeUser(offer.getUser(), offer.getExchange());
        StarShip starship = starShipService.getById(user.getLocation().getId());

        float freeAvailableVolume = spaceTechEngine.calculateFreeAvailableVolume(starship);
        Long productQuantity = getProductQuantityByFreeVolume(offer, freeAvailableVolume);

        productEngine.addProductToSpaceTech(starship, offer.getProduct(), productQuantity);

        if (offer.getProductAmount() - productQuantity > 0) {
            //TODO: implement drop to space product functionality
        }
    }

    @Override
    public void transferObject(ExchangeOffer offer) {
        User user = exchangeEngine.retrieveAnotherExchangeUser(offer.getUser(), offer.getExchange());
        StarShip starship = starShipService.getById(user.getLocation().getId());

        if (spaceTechEngine.isThereEnoughSpaceForObjects(starship, List.of(offer.getInventoryObject()))) {
            inventoryEngine.putToInventory(starship, List.of(offer.getInventoryObject()));
        } else {
            inventoryEngine.dropToSpaceExplosion(starship, List.of(offer.getInventoryObject()));
        }
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

    @Override
    public void createMoneyOffer(ExchangeOffer exchangeOffer) {
        userService.decreaseUserBalance(exchangeOffer.getUser(), exchangeOffer.getMoneyAmount());
    }

    private Long getProductQuantityByFreeVolume(ExchangeOffer offer, Float freeSpaceshipVolume) {
        float totalProductVolume = offer.getProduct().getBulk() * offer.getProductAmount();
        float residualVolume = freeSpaceshipVolume - totalProductVolume;

        if (residualVolume < 0) {
            long excessProductQuantity = (long) (residualVolume / offer.getProduct().getBulk());
            return offer.getProductAmount() - excessProductQuantity;
        }

        return offer.getProductAmount();
    }

}
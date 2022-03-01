package io.solar.facade.marketplace;

import io.solar.config.properties.MarketplaceProperties;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.entity.User;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.entity.objects.Station;
import io.solar.mapper.marketplace.MarketplaceLotMapper;
import io.solar.service.StarShipService;
import io.solar.service.StationService;
import io.solar.service.UserService;
import io.solar.service.engine.interfaces.HangarEngine;
import io.solar.service.engine.interfaces.inventory.InventoryEngine;
import io.solar.service.engine.interfaces.NotificationEngine;
import io.solar.service.engine.interfaces.ObjectEngine;
import io.solar.service.engine.interfaces.SpaceTechEngine;
import io.solar.service.marketplace.MarketplaceLotService;
import io.solar.specification.MarketplaceLotSpecification;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarketplaceLotFacade {

    private final UserService userService;
    private final MarketplaceLotService marketplaceLotService;
    private final StationService stationService;
    private final StarShipService starShipService;
    private final InventoryEngine inventoryEngine;
    private final NotificationEngine notificationEngine;
    private final HangarEngine hangarEngine;
    private final SpaceTechEngine spaceTechEngine;
    private final ObjectEngine objectEngine;
    private final MarketplaceLotMapper marketplaceLotMapper;
    private final MarketplaceProperties marketplaceProperties;


    public Page<MarketplaceLotDto> findAll(Pageable pageable, MarketplaceLotFilter filter) {

        return marketplaceLotService.findAll(pageable, new MarketplaceLotSpecification(filter))
                .map(marketplaceLotMapper::toDto);
    }

    public HttpStatus pickUpLot(User user, Long lotId) {
        MarketplaceLot lot = marketplaceLotService.getById(lotId);
        StarShip starship = starShipService.getById(user.getLocation().getId());

        if (isUserCanPickUpLot(user, lot)) {
            transferLot(user, lot, starship);
            lot.setIsBuyerHasTaken(true);
        } else {
            return HttpStatus.FORBIDDEN;
        }

        marketplaceLotService.deleteIfBothHasTaken(lot);

        return HttpStatus.OK;
    }

    public HttpStatus takeMoney(User user, Long lotId) {
        MarketplaceLot lot = marketplaceLotService.getById(lotId);

        if (isSellerCanTakeMoney(user, lot)) {
            MarketplaceBet winningBet = marketplaceLotService.getCurrentBet(lot);
            userService.increaseUserBalance(user, winningBet.getAmount());
            lot.setIsSellerHasTaken(true);
        } else {
            return HttpStatus.BAD_REQUEST;
        }

        marketplaceLotService.deleteIfBothHasTaken(lot);

        return HttpStatus.OK;
    }

    public HttpStatus createLot(MarketplaceLotDto dto, User owner) {
        MarketplaceLot lot = marketplaceLotMapper.toEntity(dto);
        lot.setOwner(owner);
        lot.setIsBuyerHasTaken(false);
        lot.setIsSellerHasTaken(false);

        if (dto.getStartDate() == null) {
            createAtTheMomentLot(dto, lot);
        } else {
            createLotWithDelay(dto, lot);
        }

        Optional<StarShip> optionalStarShip = starShipService.findById(lot.getObject().getId());

        if (optionalStarShip.isPresent()) {
            StarShip ship = optionalStarShip.get();
            if (hangarEngine.isUserAndShipAreInTheSameHangar(owner, ship)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User can't sell this ship because ship and user are not at the same station");
            }
            if (!spaceTechEngine.isUserOwnsThisSpaceTech(owner, ship)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User can't sell this ship because it isn't his");
            }

            hangarEngine.moveToMarketplace(ship, owner);
        } else {
            if (!isOwnerIsAbleToSellThisItem(owner, lot.getObject())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User can't sell this item. It isn't in his ship");
            }
            inventoryEngine.moveToMarketplace(lot.getObject());
        }

        userService.decreaseUserBalance(owner, calculateCommission(lot));
        marketplaceLotService.save(lot);
        return HttpStatus.OK;
    }

    public HttpStatus instantPurchase(MarketplaceLotDto dto, User buyer) {
        MarketplaceLot lot = marketplaceLotService.getById(dto.getId());
        Instant now = Instant.now();

        if (lot.getOwner().equals(buyer) || now.isAfter(lot.getFinishDate())) {
            return HttpStatus.BAD_REQUEST;
        }

        userService.decreaseUserBalance(buyer, lot.getInstantPrice());
        userService.increaseUserBalance(lot.getOwner(), lot.getInstantPrice());
        inventoryEngine.putToInventory(starShipService.getById(buyer.getLocation().getId()), List.of(lot.getObject()));
        notificationEngine.sendInstantPurchaseNotification(lot.getOwner(), marketplaceLotMapper.toDto(lot));
        marketplaceLotService.delete(lot);
        return HttpStatus.OK;
    }

    public HttpStatus takeAwayExpiredLot(MarketplaceLotDto dto, User seller) {
        MarketplaceLot expiredLot = marketplaceLotService.getById(dto.getId());

        if (isSellerCanTakeAwayExpiredLot(seller, expiredLot)) {
            inventoryEngine.putToInventory(starShipService.getById(seller.getLocation().getId()), List.of(expiredLot.getObject()));
            marketplaceLotService.delete(expiredLot);
        } else {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }

    private void transferLot(User user, MarketplaceLot lot, StarShip starship) {
        if (objectEngine.isObjectAStarship(lot.getObject())) {
            StarShip starshipLot = starShipService.getById(lot.getObject().getId());
            Station station = stationService.getById(user.getLocation().getAttachedToShip().getId());

            hangarEngine.moveToHangar(user, starshipLot, station);
        } else {
            inventoryEngine.putToInventory(starship, List.of(lot.getObject()));
        }
    }

    private void createLotWithDelay(MarketplaceLotDto dto, MarketplaceLot lot) {
        lot.setStartDate(dto.getStartDate());
        lot.setFinishDate(dto.getStartDate().plusSeconds(dto.getDurationSeconds()));
    }

    private void createAtTheMomentLot(MarketplaceLotDto dto, MarketplaceLot lot) {
        Instant now = Instant.now();
        lot.setStartDate(now);
        lot.setFinishDate(now.plusSeconds(dto.getDurationSeconds()));
    }

    private long calculateCommission(MarketplaceLot lot) {
        return Math.round(lot.getStartPrice() * marketplaceProperties.getCommissionPercent() * 0.01);
    }

    private boolean isUserCanPickUpLot(User user, MarketplaceLot lot) {
        MarketplaceBet winningBet = marketplaceLotService.getCurrentBet(lot);

        return (!lot.getIsBuyerHasTaken() && lot.getFinishDate().isBefore(Instant.now()) && winningBet.getUser().equals(user));
    }

    private boolean isSellerCanTakeMoney(User seller, MarketplaceLot lot) {
        return (!lot.getIsSellerHasTaken() && lot.getFinishDate().isBefore(Instant.now()) && lot.getOwner().equals(seller));
    }

    private boolean isSellerCanTakeAwayExpiredLot(User seller, MarketplaceLot lot) {
        return (lot.getFinishDate().isBefore(Instant.now()) && lot.getBets().isEmpty() && lot.getOwner().equals(seller));
    }

    private boolean isOwnerIsAbleToSellThisItem(User owner, BasicObject object) {
        return owner.getLocation().equals(object.getAttachedToShip());
    }
}
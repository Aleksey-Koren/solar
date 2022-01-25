package io.solar.facade.marketplace;

import io.solar.config.properties.MarketplaceProperties;
import io.solar.config.properties.MessengerProperties;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.dto.messenger.NotificationDto;
import io.solar.entity.User;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.messenger.NotificationType;
import io.solar.entity.objects.StarShip;
import io.solar.facade.UserFacade;
import io.solar.mapper.marketplace.MarketplaceLotMapper;
import io.solar.service.StarShipService;
import io.solar.service.engine.interfaces.InventoryEngine;
import io.solar.service.marketplace.MarketplaceLotService;
import io.solar.specification.MarketplaceLotSpecification;
import io.solar.specification.filter.MarketplaceLotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarketplaceLotFacade {

    private final MarketplaceLotService marketplaceLotService;
    private final MarketplaceLotMapper marketplaceLotMapper;
    private final MarketplaceProperties marketplaceProperties;
    private final UserFacade userFacade;
    private final InventoryEngine inventoryEngine;
    private final MessengerProperties messengerProperties;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StarShipService starShipService;

    public Page<MarketplaceLotDto> findAll(Pageable pageable, MarketplaceLotFilter filter) {

        return marketplaceLotService.findAll(pageable, new MarketplaceLotSpecification(filter))
                .map(marketplaceLotMapper::toDto);
    }

    public HttpStatus pickUpLot(User user, Long lotId) {
        MarketplaceLot lot = marketplaceLotService.getById(lotId);
        StarShip starship = starShipService.getById(user.getLocation().getId());

        if (isUserCanPickUpLot(user, lot)) {
            inventoryEngine.putToInventory(starship, List.of(lot.getObject()));
            lot.setIsBuyerHasTaken(true);
        } else {
            return HttpStatus.FORBIDDEN;
        }

        marketplaceLotService.checkLotForDelete(lot);

        return HttpStatus.OK;
    }

    public HttpStatus takeMoney(User user, Long lotId) {
        MarketplaceLot lot = marketplaceLotService.getById(lotId);

        if (isSellerCanTakeMoney(user, lot)) {
            MarketplaceBet winningBet = marketplaceLotService.getCurrentBet(lot);
            userFacade.increaseUserBalance(user, winningBet.getAmount());
            lot.setIsSellerHasTaken(true);
        } else {
            return HttpStatus.BAD_REQUEST;
        }

        marketplaceLotService.checkLotForDelete(lot);

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

        inventoryEngine.moveToMarketplace(lot.getObject());
        userFacade.decreaseUserBalance(owner, calculateCommission(lot));
        marketplaceLotService.save(lot);
        return HttpStatus.OK;
    }

    public HttpStatus instantPurchase(MarketplaceLotDto dto, User buyer) {
        MarketplaceLot lot = marketplaceLotService.getById(dto.getId());
        Instant now = Instant.now();

        if (lot.getOwner().equals(buyer) || now.isAfter(lot.getFinishDate())) {
            return HttpStatus.BAD_REQUEST;
        }

        userFacade.decreaseUserBalance(buyer, lot.getInstantPrice());
        userFacade.increaseUserBalance(lot.getOwner(), lot.getInstantPrice());
        inventoryEngine.putToInventory(starShipService.getById(buyer.getLocation().getId()), List.of(lot.getObject()));
        sendInstantPurchaseNotification(lot);
        marketplaceLotService.delete(lot);
        return HttpStatus.OK;
    }

    private void sendInstantPurchaseNotification(MarketplaceLot lot) {
        simpMessagingTemplate.convertAndSendToUser(lot.getOwner().getLogin()
                , messengerProperties.getNotificationDestination()
                , new NotificationDto<>(NotificationType.INSTANT_PURCHASE.name(), marketplaceLotMapper.toDto(lot)));
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
}
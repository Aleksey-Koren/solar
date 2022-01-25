package io.solar.facade.marketplace;

import io.solar.config.properties.MarketplaceProperties;
import io.solar.config.properties.MessengerProperties;
import io.solar.dto.marketplace.MarketplaceLotDto;
import io.solar.dto.messenger.NotificationDto;
import io.solar.entity.User;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.messenger.NotificationType;
import io.solar.facade.UserFacade;
import io.solar.mapper.marketplace.MarketplaceLotMapper;
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

    public Page<MarketplaceLotDto> findAll(Pageable pageable, MarketplaceLotFilter filter) {

        return marketplaceLotService.findAll(pageable, new MarketplaceLotSpecification(filter))
                .map(marketplaceLotMapper::toDto);
    }

    public HttpStatus pickUpLot(User user, Long lotId) {
        MarketplaceLot lot = marketplaceLotService.getById(lotId);

        if (!lot.getIsBuyerHasTaken() && isUserWinner(user, lot)) {
            inventoryEngine.putToInventory(user.getLocation(), List.of(lot.getObject()));
            lot.setIsBuyerHasTaken(true);
        } else {
            return HttpStatus.FORBIDDEN;
        }

        updateLot(lot);

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
        inventoryEngine.putToInventory(buyer.getLocation(), List.of(lot.getObject()));
        sendInstantPurchaseNotification(lot);
        marketplaceLotService.delete(lot);
        return HttpStatus.OK;
    }

    private void updateLot(MarketplaceLot lot) {
        if (lot.getIsBuyerHasTaken() && lot.getIsSellerHasTaken()) {
            marketplaceLotService.delete(lot);
        } else {
            marketplaceLotService.save(lot);
        }
    }

    private boolean isUserWinner(User user, MarketplaceLot lot) {
        MarketplaceBet winningBet = marketplaceLotService.findCurrentBet(lot)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find current bet for lot: " + lot.getId()));

        return winningBet.getUser().equals(user);
    }

    private void sendInstantPurchaseNotification(MarketplaceLot lot) {
        simpMessagingTemplate.convertAndSendToUser(lot.getOwner().getLogin()
                , messengerProperties.getNotificationDestination()
                , new NotificationDto<>(NotificationType.INSTANT_PURCHASE.name(), marketplaceLotMapper.toDto(lot)));
    }


    private long calculateCommission(MarketplaceLot lot) {
        return Math.round(lot.getStartPrice() * marketplaceProperties.getCommissionPercent() * 0.01);
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
}
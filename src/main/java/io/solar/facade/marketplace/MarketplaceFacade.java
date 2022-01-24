 package io.solar.facade.marketplace;

 import io.solar.config.properties.MarketplaceProperties;
 import io.solar.config.properties.MessengerProperties;
 import io.solar.dto.marketplace.MarketplaceBetDto;
 import io.solar.dto.marketplace.MarketplaceLotDto;
 import io.solar.dto.messenger.NotificationDto;
 import io.solar.entity.User;
 import io.solar.entity.marketplace.MarketplaceBet;
 import io.solar.entity.marketplace.MarketplaceLot;
 import io.solar.entity.messenger.NotificationType;
 import io.solar.facade.UserFacade;
 import io.solar.mapper.marketplace.MarketplaceBetMapper;
 import io.solar.mapper.marketplace.MarketplaceLotMapper;
 import io.solar.service.engine.interfaces.InventoryEngine;
 import io.solar.service.marketplace.MarketplaceBetService;
 import io.solar.service.marketplace.MarketplaceLotService;
 import lombok.RequiredArgsConstructor;
 import org.springframework.http.HttpStatus;
 import org.springframework.messaging.simp.SimpMessagingTemplate;
 import org.springframework.stereotype.Component;

 import java.time.Instant;
 import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MarketplaceFacade {



    private final MarketplaceLotService marketplaceLotService;
    private final MarketplaceLotMapper marketplaceLotMapper;
    private final MarketplaceBetMapper marketplaceBetMapper;
    private final MarketplaceProperties marketplaceProperties;
    private final MarketplaceBetService marketplaceBetService;
    private final UserFacade userFacade;
    private final InventoryEngine inventoryEngine;
    private final MessengerProperties messengerProperties;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    public HttpStatus makeBet(MarketplaceBetDto dto, User user) {
        MarketplaceLot lot = marketplaceLotService.getById(dto.getLotId());
        Instant now = Instant.now();

        if (/*lot.getOwner().equals(user) ||*/ now.isAfter(lot.getFinishDate())) {
            return HttpStatus.BAD_REQUEST;
        }

        MarketplaceBet newBet = marketplaceBetMapper.toEntity(dto);
        newBet.setUser(user);

        Optional<MarketplaceBet> currentBet = marketplaceLotService.findCurrentBet(lot);

        if (validateNewBet(newBet, currentBet, lot)) {
            newBet.setBetTime(now);
            marketplaceBetService.save(newBet);
            sendOverbidNotification(lot, currentBet);
            return HttpStatus.OK;
        }else{
            return HttpStatus.BAD_REQUEST;
        }
    }

    private boolean validateNewBet(MarketplaceBet newBet, Optional<MarketplaceBet> currentBet, MarketplaceLot lot) {
        long betStep = Math.round(lot.getStartPrice() * marketplaceProperties.getBetStepPercents() * 0.01);

        if (currentBet.isPresent()) {
            return newBet.getAmount() >= currentBet.get().getAmount() + betStep;
        }else {
            return newBet.getAmount() >= lot.getStartPrice() + betStep;
        }
    }

    private void sendOverbidNotification(MarketplaceLot lot, Optional<MarketplaceBet> currentBet) {
        if (currentBet.isPresent()) {
            MarketplaceBet current = currentBet.get();
            simpMessagingTemplate.convertAndSendToUser(current.getUser().getLogin()
                    , messengerProperties.getNotificationDestination()
                    , new NotificationDto<>(NotificationType.BET_OVERBID.name(), marketplaceLotMapper.toDto(lot)));
        }
    }
}

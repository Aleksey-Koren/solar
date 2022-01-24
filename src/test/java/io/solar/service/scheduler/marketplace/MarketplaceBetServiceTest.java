package io.solar.service.scheduler.marketplace;

import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.repository.marketplace.MarketplaceBetRepository;
import io.solar.repository.marketplace.MarketplaceLotRepository;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.marketplace.MarketplaceBetService;
import io.solar.service.marketplace.MarketplaceLotService;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@SpringBootTest
public class MarketplaceBetServiceTest {

    @Autowired
    private MarketplaceLotService marketplaceLotService;
    @Autowired
    private StarShipService starShipService;
    @Autowired
    private UserService userService;
    @Autowired
    private MarketplaceBetService marketplaceBetService;
    @Autowired
    private MarketplaceLotRepository marketplaceLotRepository;
    @Autowired
    private MarketplaceBetRepository marketplaceBetRepository;

    @RepeatedTest(30)
    @Transactional
    @Rollback(false)
    void save_shouldSaveLotBet() {
        MarketplaceLot lot = MarketplaceLot.builder()
                .object(starShipService.getById(744L))
                .owner(userService.getById(2L))
                .startDate(Instant.now())
                .finishDate(Instant.now().plusSeconds(3600 * 24))
                .startPrice(400L)
                .instantPrice(800L)
                .isBuyerHasTaken(false)
                .isSellerHasTaken(false)
                .build();

        marketplaceLotService.save(lot);
        MarketplaceBet bet = MarketplaceBet.builder()
                .lot(lot)
                .user(userService.getById(2L))
                .amount(450L)
                .build();
        marketplaceBetService.save(bet);
    }

    @Test
    void save_shouldOverrideBet() {
        MarketplaceBet bet = MarketplaceBet.builder()
                .lot(marketplaceLotService.getById(1L))
                .user(userService.getById(2L))
                .amount(799L)
                .build();
        marketplaceBetService.save(bet);
    }

    @Test
    @Transactional
    @Rollback(false)
    void a() {
        MarketplaceLot lot = marketplaceLotService.getById(1L);
        lot.setCurrentBet(MarketplaceBet.builder().lot(marketplaceLotService.getById(1L))
                .user(userService.getById(2L))
                .amount(799L)
                .build());
        System.out.println(lot);
    }

    @Test
    @Transactional
    @Rollback(value = false)
    void delete ()  {
        marketplaceBetRepository.deleteById(7L);
        marketplaceLotRepository.deleteById(7L);
    }


}

package io.solar.service.scheduler.marketplace;

import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.service.StarShipService;
import io.solar.service.UserService;
import io.solar.service.marketplace.MarketplaceLotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MarketplaceLotServiceTest {

    @Autowired
    private MarketplaceLotService marketplaceLotService;
    @Autowired
    private StarShipService starShipService;
    @Autowired
    private UserService userService;

    @Test
    void save_shouldSaveEntityWithoutIdAndUpdateWithId() {
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
        assertNotNull(lot.getId());
        Long startPriceBeforeUpdate = 400L;
        assertEquals(startPriceBeforeUpdate, lot.getStartPrice());
        Long startPriceForUpdate = 500L;
        lot.setStartPrice(startPriceForUpdate);
        marketplaceLotService.save(lot);
        lot = marketplaceLotService.getById(lot.getId());
        assertEquals(startPriceForUpdate, lot.getStartPrice());


    }
}

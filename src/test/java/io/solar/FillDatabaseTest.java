package io.solar;


import io.solar.dto.messenger.CreateRoomDto;
import io.solar.entity.Goods;
import io.solar.entity.Product;
import io.solar.entity.User;
import io.solar.entity.exchange.Exchange;
import io.solar.entity.exchange.ExchangeOffer;
import io.solar.entity.exchange.OfferType;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.Room;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.StarShip;
import io.solar.facade.messenger.RoomFacade;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.StarShipRepository;
import io.solar.repository.UserRepository;
import io.solar.repository.exchange.ExchangeOfferRepository;
import io.solar.repository.exchange.ExchangeRepository;
import io.solar.repository.marketplace.MarketplaceBetRepository;
import io.solar.repository.marketplace.MarketplaceLotRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.security.Role;
import io.solar.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Start.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FillDatabaseTest {

    private final Random RANDOM = new Random();

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MarketplaceLotRepository marketplaceLotRepository;
    private final MarketplaceBetRepository marketplaceBetRepository;
    private final BasicObjectRepository basicObjectRepository;
    private final ExchangeRepository exchangeRepository;
    private final StarShipRepository starShipRepository;
    private final ExchangeOfferRepository exchangeOfferRepository;
    private final RoomFacade roomFacade;
    private final UserService userService;

    @Autowired
    public FillDatabaseTest(UserRepository userRepository,
                            RoomRepository roomRepository,
                            MessageRepository messageRepository,
                            MarketplaceBetRepository marketplaceBetRepository,
                            ExchangeRepository exchangeRepository,
                            ExchangeOfferRepository exchangeOfferRepository,
                            UserService userService,
                            MarketplaceLotRepository marketplaceLotRepository,
                            BasicObjectRepository basicObjectRepository,
                            StarShipRepository starShipRepository,
                            RoomFacade roomFacade) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.marketplaceBetRepository = marketplaceBetRepository;
        this.exchangeRepository = exchangeRepository;
        this.exchangeOfferRepository = exchangeOfferRepository;
        this.userService = userService;
        this.marketplaceLotRepository = marketplaceLotRepository;
        this.basicObjectRepository = basicObjectRepository;
        this.starShipRepository = starShipRepository;
        this.roomFacade = roomFacade;
    }

    @Order(1)
    @RepeatedTest(100)
    public void createUsers() {
        User user = User.builder()
                .login(UUID.randomUUID().toString())
                .password("pass")
                .money(1000L)
                .hackAttempts(0)
                .build();

        userService.registerNewUser(user, Role.USER);
    }

    @Order(2)
    @RepeatedTest(100)
    @Transactional
    @Commit
    public void createRooms() {
        roomFacade.createRoom(CreateRoomDto.builder()
                .isPrivate(false)
                .userId(findRandomUser().getId())
                .build(), findRandomUser());
    }

    @Order(3)
    @RepeatedTest(100)
    public void createMessages() {

        Message message = Message.builder()
                .message(RandomString.make())
                .sender(findRandomUser())
                .room(findRandomRoom())
                .createdAt(Instant.now().minus(RANDOM.nextInt(1000), ChronoUnit.DAYS))
                .messageType(MessageType.CHAT)
                .build();

        messageRepository.save(message);
    }

    @Order(4)
    @RepeatedTest(100)
    public void createMarketplaceLots() {
        MarketplaceLot lot = MarketplaceLot.builder()
                .object(findRandomObject())
                .owner(findRandomUser())
                .startDate(Instant.now())
                .finishDate(Instant.now().plusSeconds(RANDOM.nextInt(5000)))
                .startPrice((long) RANDOM.nextInt(1_000_000))
                .instantPrice((long) RANDOM.nextInt(1_000_000))
                .isBuyerHasTaken(false)
                .isSellerHasTaken(false)
                .build();

        marketplaceLotRepository.save(lot);
    }

    @Order(5)
    @RepeatedTest(100)
    public void createMarketplaceBets() {
        MarketplaceBet bet = MarketplaceBet.builder()
                .lot(findRandomLot())
                .user(findRandomUser())
                .amount((long) RANDOM.nextInt(100000))
                .betTime(Instant.now().plusSeconds(RANDOM.nextInt(10000)))
                .build();

        marketplaceBetRepository.save(bet);
    }

    @Order(6)
    @RepeatedTest(1)
    public void createExchanges() {
        List<User> users = userRepository.findAll();

        int iteration = 0;
        while (iteration < users.size() - 1) {

            exchangeRepository.save(
                    Exchange.builder()
                            .firstUser(users.get(iteration))
                            .secondUser(users.get(iteration + 1))
                            .startTime(Instant.now())
                            .firstAccepted(false)
                            .secondAccepted(false)
                            .build()
            );

            iteration += 2;
        }
    }

    @Order(7)
    @RepeatedTest(100)
    public void createExchangeOffers() {
        Exchange randomExchange = findRandomExchange();
        User user = RANDOM.nextBoolean() ? randomExchange.getFirstUser() : randomExchange.getSecondUser();

        ExchangeOffer offer = ExchangeOffer.builder()
                .exchange(randomExchange)
                .createdAt(Instant.now())
                .user(user)
                .offerType(OfferType.values()[Math.abs(RANDOM.nextInt(0, 2))])
                .moneyAmount(RANDOM.nextLong(0, 5000))
                .inventoryObject(findRandomObject())
                .build();

        exchangeOfferRepository.save(offer);
    }

    private User findRandomUser() {
        List<User> users = userRepository.findAll();

        return users.get(RANDOM.nextInt(0, users.size()));
    }

    private Room findRandomRoom() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.get(RANDOM.nextInt(0, rooms.size()));
    }

    private MarketplaceLot findRandomLot() {
        List<MarketplaceLot> lots = marketplaceLotRepository.findAll();

        return lots.get(RANDOM.nextInt(0, lots.size()));
    }

    private BasicObject findRandomObject() {
        List<BasicObject> objects = basicObjectRepository.findAll();

        return objects.get(RANDOM.nextInt(0, objects.size()));
    }

    private Exchange findRandomExchange() {
        List<Exchange> exchanges = exchangeRepository.findAll();

        return exchanges.get(RANDOM.nextInt(0, exchanges.size()));
    }
}
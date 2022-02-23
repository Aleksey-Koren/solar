package io.solar;


import io.solar.dto.messenger.CreateRoomDto;
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
import io.solar.entity.modification.Modification;
import io.solar.entity.modification.ModificationPrice;
import io.solar.entity.modification.ModificationType;
import io.solar.entity.modification.ParameterModification;
import io.solar.entity.modification.ParameterType;
import io.solar.entity.objects.BasicObject;
import io.solar.entity.objects.ObjectTypeDescription;
import io.solar.entity.objects.Station;
import io.solar.entity.price.Price;
import io.solar.entity.price.PriceProduct;
import io.solar.facade.messenger.RoomFacade;
import io.solar.repository.BasicObjectRepository;
import io.solar.repository.ObjectTypeDescriptionRepository;
import io.solar.repository.ProductRepository;
import io.solar.repository.StationRepository;
import io.solar.repository.UserRepository;
import io.solar.repository.exchange.ExchangeOfferRepository;
import io.solar.repository.exchange.ExchangeRepository;
import io.solar.repository.marketplace.MarketplaceBetRepository;
import io.solar.repository.marketplace.MarketplaceLotRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import io.solar.repository.modification.ModificationPriceRepository;
import io.solar.repository.modification.ModificationRepository;
import io.solar.repository.modification.ModificationTypeRepository;
import io.solar.repository.modification.ParameterModificationRepository;
import io.solar.repository.price.PriceProductRepository;
import io.solar.repository.price.PriceRepository;
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
    private final PriceRepository priceRepository;
    private final ExchangeOfferRepository exchangeOfferRepository;
    private final ProductRepository productRepository;
    private final RoomFacade roomFacade;
    private final UserService userService;
    private final PriceProductRepository priceProductRepository;
    private final ModificationTypeRepository modificationTypeRepository;
    private final ModificationRepository modificationRepository;
    private final ParameterModificationRepository parameterModificationRepository;
    private final ModificationPriceRepository modificationPriceRepository;
    private final ObjectTypeDescriptionRepository objectTypeDescriptionRepository;
    private final StationRepository stationRepository;

    @Autowired
    public FillDatabaseTest(UserRepository userRepository,
                            RoomRepository roomRepository,
                            MessageRepository messageRepository,
                            MarketplaceBetRepository marketplaceBetRepository,
                            ExchangeRepository exchangeRepository,
                            PriceRepository priceRepository,
                            ExchangeOfferRepository exchangeOfferRepository,
                            UserService userService,
                            MarketplaceLotRepository marketplaceLotRepository,
                            BasicObjectRepository basicObjectRepository,
                            ProductRepository productRepository,
                            RoomFacade roomFacade,
                            PriceProductRepository priceProductRepository,
                            ModificationTypeRepository modificationTypeRepository,
                            ModificationRepository modificationRepository,
                            ParameterModificationRepository parameterModificationRepository,
                            ModificationPriceRepository modificationPriceRepository,
                            ObjectTypeDescriptionRepository objectTypeDescriptionRepository,
                            StationRepository stationRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.marketplaceBetRepository = marketplaceBetRepository;
        this.exchangeRepository = exchangeRepository;
        this.priceRepository = priceRepository;
        this.exchangeOfferRepository = exchangeOfferRepository;
        this.userService = userService;
        this.marketplaceLotRepository = marketplaceLotRepository;
        this.basicObjectRepository = basicObjectRepository;
        this.productRepository = productRepository;
        this.roomFacade = roomFacade;
        this.priceProductRepository = priceProductRepository;
        this.modificationTypeRepository = modificationTypeRepository;
        this.modificationRepository = modificationRepository;
        this.parameterModificationRepository = parameterModificationRepository;
        this.modificationPriceRepository = modificationPriceRepository;
        this.objectTypeDescriptionRepository = objectTypeDescriptionRepository;
        this.stationRepository = stationRepository;
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
        for (int i = 0; i < users.size() - 1; i += 2) {
            exchangeRepository.save(
                    Exchange.builder()
                            .firstUser(users.get(i))
                            .secondUser(users.get(i + 1))
                            .startTime(Instant.now())
                            .firstAccepted(false)
                            .secondAccepted(false)
                            .build()
            );
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

    @Order(8)
    @RepeatedTest(100)
    public void createPrices() {
        Price price = Price.builder()
                .moneyAmount(RANDOM.nextLong(1, 100_000))
                .build();

        price.setOwners(List.of(findRandomUser(), findRandomUser(), findRandomUser(), findRandomUser(), findRandomUser()));

        priceRepository.save(price);
    }

    @Order(9)
    @RepeatedTest(200)
    public void createPriceProducts() {
        PriceProduct priceProduct = PriceProduct.builder()
                .product(findRandomProduct())
                .price(findRandomPrice())
                .productAmount(RANDOM.nextInt(1, 1000))
                .build();

        priceProductRepository.save(priceProduct);
    }

    @Order(10)
    @RepeatedTest(100)
    public void createModificationTypes() {
        ModificationType modificationType = ModificationType.builder()
                .title(RandomString.make())
                .build();

        modificationTypeRepository.save(modificationType);
    }

    @Order(11)
    @RepeatedTest(100)
    public void createModifications() {
        Modification modification = Modification.builder()
                .modificationType(findRandomModificationType())
                .level((byte) RANDOM.nextInt(1, 5))
                .description(RandomString.make())
                .availableObjectTypeDescriptions(List.of(findRandomOTD(), findRandomOTD(), findRandomOTD(), findRandomOTD()))
                .build();

        modificationRepository.save(modification);
    }

    @Order(12)
    @RepeatedTest(100)
    public void createModificationPrices() {
        ModificationPrice modificationPrice = ModificationPrice.builder()
                .station(findRandomStation())
                .modification(findRandomModification())
                .price(findRandomPrice())
                .build();

        modificationPriceRepository.save(modificationPrice);
    }

    @Order(13)
    @RepeatedTest(100)
    public void createParameterModifications() {
        ParameterModification parameterModification = ParameterModification.builder()
                .modificationValue(RANDOM.nextDouble(1.0, 100.0))
                .modification(findRandomModification())
                .parameterType(ParameterType.values()[RANDOM.nextInt(0, ParameterType.values().length - 1)])
                .build();

        parameterModificationRepository.save(parameterModification);
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

    private Price findRandomPrice() {
        List<Price> prices = priceRepository.findAll();

        return prices.get(RANDOM.nextInt(0, prices.size()));
    }

    private Product findRandomProduct() {
        List<Product> products = productRepository.findAll();

        return products.get(RANDOM.nextInt(0, products.size()));
    }

    private ModificationType findRandomModificationType() {
        List<ModificationType> modificationTypes = modificationTypeRepository.findAll();

        return modificationTypes.get(RANDOM.nextInt(0, modificationTypes.size()));
    }

    private ObjectTypeDescription findRandomOTD() {
        List<ObjectTypeDescription> all = objectTypeDescriptionRepository.findAll();

        return all.get(RANDOM.nextInt(0, all.size()));
    }

    private Modification findRandomModification() {
        List<Modification> modifications = modificationRepository.findAll();

        return modifications.get(RANDOM.nextInt(0, modifications.size()));
    }

    private Station findRandomStation() {
        List<Station> stations = stationRepository.findAll();

        return stations.get(RANDOM.nextInt(0, stations.size()));
    }
}
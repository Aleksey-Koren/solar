package io.solar;


import io.solar.dto.messenger.CreateRoomDto;
import io.solar.entity.User;
import io.solar.entity.marketplace.MarketplaceBet;
import io.solar.entity.marketplace.MarketplaceLot;
import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.MessageType;
import io.solar.entity.messenger.Room;
import io.solar.facade.messenger.RoomFacade;
import io.solar.repository.StarShipRepository;
import io.solar.repository.UserRepository;
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
    private final StarShipRepository starshipRepository;
    private final MarketplaceLotRepository marketplaceLotRepository;
    private final MarketplaceBetRepository marketplaceBetRepository;
    private final RoomFacade roomFacade;
    private final UserService userService;

    @Autowired
    public FillDatabaseTest(UserRepository userRepository,
                            RoomRepository roomRepository,
                            MessageRepository messageRepository,
                            StarShipRepository starshipRepository,
                            MarketplaceBetRepository marketplaceBetRepository,
                            UserService userService,
                            MarketplaceLotRepository marketplaceLotRepository,
                            RoomFacade roomFacade) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.starshipRepository = starshipRepository;
        this.marketplaceBetRepository = marketplaceBetRepository;
        this.userService = userService;
        this.marketplaceLotRepository = marketplaceLotRepository;
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
                .object(starshipRepository.getById(744L))
                .owner(findRandomUser())
                .startDate(Instant.now())
                .finishDate(Instant.now().plusSeconds(RANDOM.nextInt(5000)))
                .startPrice(RANDOM.nextLong())
                .instantPrice(RANDOM.nextLong())
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
                .amount(RANDOM.nextLong())
                .build();

        marketplaceBetRepository.save(bet);
    }

    private User findRandomUser() {
        List<User> users = userRepository.findAll();

        return users.get(Math.abs(RANDOM.nextInt(users.size())));
    }

    private Room findRandomRoom() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.get(Math.abs(RANDOM.nextInt(rooms.size())));
    }

    private MarketplaceLot findRandomLot() {
        List<MarketplaceLot> lots = marketplaceLotRepository.findAll();

        return lots.get(Math.abs(RANDOM.nextInt(lots.size())));
    }
}
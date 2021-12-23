package io.solar;


import io.solar.entity.User;

import io.solar.entity.messenger.Message;
import io.solar.entity.messenger.Room;
import io.solar.entity.messenger.UserRoom;

import io.solar.repository.UserRepository;
import io.solar.repository.messenger.MessageRepository;
import io.solar.repository.messenger.RoomRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Start.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled
public class FillDatabaseTest {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FillDatabaseTest(UserRepository userRepository,
                            RoomRepository roomRepository,
                            MessageRepository messageRepository,
                            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Order(1)
    @RepeatedTest(100)
    public void createUsers() {

        User user = User.builder()
                .login(UUID.randomUUID().toString())
                .password(passwordEncoder.encode("pass"))
                .money(1000L)
                .hackAttempts(0)
                .build();

        userRepository.save(user);
    }

    @Order(2)
    @RepeatedTest(100)
    public void createRooms() {
        Room room = new Room(null, UUID.randomUUID().toString(), Instant.now(), findRandomUser());

        roomRepository.save(room);
    }

    @Order(3)
    @RepeatedTest(100)
    public void createMessages() {

        Message message = Message.builder()
                .message(RandomString.make())
                .sender(findRandomUser())
                .room(findRandomRoom())
                .createdAt(Instant.now().minus(new Random().nextInt(1000), ChronoUnit.DAYS))
                .build();

        messageRepository.save(message);
    }

    @Order(4)
    @RepeatedTest(100)
    public void createUserRooms() {
        User randomUser = findRandomUser();

        System.out.println("id:" + randomUser.getId());

        randomUser.setUserRooms(List.of(UserRoom.builder()
                .user(randomUser)
                .room(findRandomRoom())
                .subscribedAt(Instant.now().minus(new Random().nextInt(1000), ChronoUnit.DAYS))
                .lastSeenAt(Instant.now())
                .build()));

        userRepository.save(randomUser);
    }


    private User findRandomUser() {
        List<User> users = userRepository.findAll();

        return users.get(Math.abs(new Random().nextInt(users.size())));
    }

    private Room findRandomRoom() {
        List<Room> rooms = roomRepository.findAll();

        return rooms.get(Math.abs(new Random().nextInt(rooms.size())));
    }
}

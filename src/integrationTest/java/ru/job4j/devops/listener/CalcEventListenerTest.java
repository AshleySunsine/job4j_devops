package ru.job4j.devops.listener;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import ru.job4j.devops.config.ContainersConfig;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;
import ru.job4j.devops.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CalcEventListenerTest extends ContainersConfig {

    private static final String SUM_CALC_TYPE = "SUM";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CalcEventRepository calcEventRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() throws Exception {
        createTopics();
    }

    @Test
    void whenCalculationSum() {
        var event = new CalcEvent();
        event.setType(SUM_CALC_TYPE);
        event.setCreateDate(LocalDateTime.ofInstant(
                Instant.now(), ZoneOffset.UTC));
        event.setResult(4);
        event.setFirst(2);
        event.setSecond(2);
        User user = new User();
        user.setUsername("Job4j new member : " + System.nanoTime());
        kafkaTemplate.send("signup", user);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    User optionalUser = userRepository.findByUsername(user.getUsername()).get();
                    event.setUser(optionalUser);
                });

        kafkaTemplate.send("calcEvent", event);

        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    var optionalUser = calcEventRepository.findByType(event.getType());
                    assertNotNull(optionalUser);
                    assertEquals(optionalUser.size(), 1);
                });
    }
}

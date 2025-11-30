package ru.job4j.devops.listener;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;
import ru.job4j.devops.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CalcEventListenerTest {

    private static final String SUM_CALC_TYPE = "SUM";
    private static final String KAFKA_IMAGE = "apache/kafka:3.7.2";
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    ).waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(15));

    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse(KAFKA_IMAGE))
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(120));

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CalcEventRepository calcEventRepository;

    @Autowired
    private UserRepository userRepository;

    public static void createTopics() throws Exception {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers()
        ))) {
            NewTopic topic = new NewTopic("signup", 1, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
            topic = new NewTopic("calcEvent", 1, (short) 1);
            adminClient.createTopics(List.of(topic)).all().get();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        POSTGRES.start();
        KAFKA.start();
        createTopics();
    }

    @AfterAll
    static void afterAll() {
        POSTGRES.stop();
        KAFKA.stop();
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        //  Добавьте  эти  строки  для  логирования  SQL  и  Hibernate
        registry.add("spring.jpa.show-sql", () -> true);
        registry.add("spring.jpa.properties.hibernate.format_sql", () -> true);
        registry.add("logging.level.org.hibernate.SQL", () -> "DEBUG");
        registry.add("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", () -> "TRACE");
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

package ru.job4j.devops.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;
import ru.job4j.devops.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@Disabled("Появились интеграционные тесты")
class CalcServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(15));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CalcService calcService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalcEventRepository calcEventRepository;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        calcEventRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void whenAddNumbersThenEventSaved() {
        User user = new User();
        user.setName("testuser");
        User savedUser = userRepository.save(user);

        int first = 5;
        int second = 3;
        int result = calcService.add(savedUser, first, second);

        assertThat(result).isEqualTo(8);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(1);

        CalcEvent event = events.get(0);
        assertThat(event.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(event.getFirst()).isEqualTo(first);
        assertThat(event.getSecond()).isEqualTo(second);
        assertThat(event.getResult()).isEqualTo(result);
        assertThat(event.getType()).isEqualTo("ADDITION");
        assertThat(event.getCreateDate()).isNotNull();
    }

    @Test
    void whenAddNegativeNumbersThenEventSaved() {
        User user = new User();
        user.setName("testuser2");
        User savedUser = userRepository.save(user);

        int result = calcService.add(savedUser, -5, -3);

        assertThat(result).isEqualTo(-8);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(1);

        CalcEvent event = events.get(0);
        assertThat(event.getResult()).isEqualTo(-8);
        assertThat(event.getType()).isEqualTo("ADDITION");
        assertThat(event.getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void whenAddMultipleTimesThenAllEventsSaved() {
        User user = new User();
        user.setName("testuser3");
        User savedUser = userRepository.save(user);

        calcService.add(savedUser, 1, 1);
        calcService.add(savedUser, 2, 2);
        calcService.add(savedUser, 3, 3);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(3);

        assertThat(events.stream().map(CalcEvent::getResult))
                .containsExactly(2, 4, 6);

        // Проверяем что все события принадлежат одному пользователю
        assertThat(events.stream().map(e -> e.getUser().getId()))
                .containsOnly(savedUser.getId());

        // Проверяем что все события имеют правильный тип
        assertThat(events.stream().map(CalcEvent::getType))
                .containsOnly("ADDITION");
    }

    @Test
    void whenAddWithZeroThenEventSaved() {
        User user = new User();
        user.setName("testuser4");
        User savedUser = userRepository.save(user);

        int result = calcService.add(savedUser, 10, 0);

        assertThat(result).isEqualTo(10);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getResult()).isEqualTo(10);
    }

    @Test
    void whenAddLargeNumbersThenEventSaved() {
        User user = new User();
        user.setName("testuser5");
        User savedUser = userRepository.save(user);

        int result = calcService.add(savedUser, Integer.MAX_VALUE, 1);

        // Проверяем переполнение (должно быть отрицательное число)
        assertThat(result).isEqualTo(Integer.MIN_VALUE);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getResult()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void whenAddForDifferentUsersThenSeparateEventsSaved() {
        User user1 = new User();
        user1.setName("user1");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        User savedUser2 = userRepository.save(user2);

        calcService.add(savedUser1, 1, 1);
        calcService.add(savedUser2, 2, 2);

        var events = calcEventRepository.findAll();
        assertThat(events).hasSize(2);

        // Проверяем что события принадлежат разным пользователям
        var userIds = events.stream()
                .map(e -> e.getUser().getId())
                .collect(Collectors.toSet());
        assertThat(userIds).containsExactlyInAnyOrder(savedUser1.getId(), savedUser2.getId());
    }

    @Test
    void whenAddThenEventHasCorrectTimestamp() {
        User user = new User();
        user.setName("testuser6");
        User savedUser = userRepository.save(user);

        LocalDateTime beforeTest = LocalDateTime.now().minusSeconds(1);
        calcService.add(savedUser, 5, 5);
        LocalDateTime afterTest = LocalDateTime.now().plusSeconds(1);

        var event = calcEventRepository.findAll().get(0);

        assertThat(event.getCreateDate())
                .isAfterOrEqualTo(beforeTest)
                .isBeforeOrEqualTo(afterTest);
    }

    @Test
    void whenFindEventsByUserThenReturnCorrectEvents() {
        User user1 = new User();
        user1.setName("user1");
        User savedUser1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        User savedUser2 = userRepository.save(user2);

        calcService.add(savedUser1, 1, 1);
        calcService.add(savedUser1, 2, 2);
        calcService.add(savedUser2, 3, 3);

        // Проверяем события для первого пользователя
        var user1Events = calcEventRepository.findAll().stream()
                .filter(e -> e.getUser().getId().equals(savedUser1.getId()))
                .collect(Collectors.toList());

        assertThat(user1Events).hasSize(2);
        assertThat(user1Events.stream().map(CalcEvent::getResult))
                .containsExactly(2, 4);
    }

    @Test
    void whenDatabaseIsEmptyThenNoEvents() {
        User user = new User();
        user.setName("testuser7");
        userRepository.save(user);

        var events = calcEventRepository.findAll();
        assertThat(events).isEmpty();
    }

    @Test
    void whenAddMultipleOperationsThenAllHaveUniqueIds() {
        User user = new User();
        user.setName("testuser8");
        User savedUser = userRepository.save(user);

        calcService.add(savedUser, 1, 1);
        calcService.add(savedUser, 2, 2);
        calcService.add(savedUser, 3, 3);

        var events = calcEventRepository.findAll();

        // Проверяем что у всех событий уникальные ID
        var eventIds = events.stream()
                .map(CalcEvent::getId)
                .collect(Collectors.toSet());
        assertThat(eventIds).hasSize(3);

        // Проверяем что ID не null
        assertThat(events).allMatch(event -> event.getId() != null);
    }
}
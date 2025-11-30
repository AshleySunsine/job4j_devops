package ru.job4j.devops.listener;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import ru.job4j.devops.config.ContainersConfig;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.UserRepository;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class UserSignUpEventListenerTest extends ContainersConfig {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void beforeAll() throws Exception {
        createTopics();
    }

    @Test
    void whenSignupNewMember() {
        var user = new User();
        user.setUsername("Job4j new member : " + System.nanoTime());
        kafkaTemplate.send("signup", user);
        await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    var optionalUser = userRepository.findByUsername(user.getUsername());
                    assertThat(optionalUser).isPresent();
                });
    }
}

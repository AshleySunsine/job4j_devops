package ru.job4j.devops.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class ContainersConfig {
    private static final String KAFKA_IMAGE = "apache/kafka:3.7.2";
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    ).waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(15));

    private static final KafkaContainer KAFKA = new KafkaContainer(
            DockerImageName.parse(KAFKA_IMAGE))
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofSeconds(120));

    static {
        POSTGRES.start();
        KAFKA.start();
    }

    public  static  void  createTopics()  throws  Exception  {
        try  (AdminClient  adminClient  =  AdminClient.create(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers()
        ))) {
            Set<String> existingTopics = adminClient.listTopics().names().get();

            if (!existingTopics.contains("signup")) {
                NewTopic signupTopic = new NewTopic("signup", 1, (short) 1);
                adminClient.createTopics(List.of(signupTopic)).all().get();
            }

            if (!existingTopics.contains("calcEvent")) {
                NewTopic calcEventTopic = new NewTopic("calcEvent", 1, (short) 1);
                adminClient.createTopics(List.of(calcEventTopic)).all().get();
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
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
}
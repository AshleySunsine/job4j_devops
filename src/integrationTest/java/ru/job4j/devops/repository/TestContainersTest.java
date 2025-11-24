package ru.job4j.devops.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestContainersTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestContainersTest.class);
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            "postgres:16-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        try {
            LOG.info("Starting PostgreSQL container...");
            POSTGRES.start();
            LOG.info("PostgreSQL container started successfully");
            LOG.info("JDBC URL: {}", POSTGRES.getJdbcUrl());
            LOG.info("Username: {}", POSTGRES.getUsername());
            LOG.info("Password: {}", POSTGRES.getPassword());
        } catch (Exception e) {
            LOG.error("Failed to start PostgreSQL container", e);
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            LOG.info("Stopping PostgreSQL container...");
            POSTGRES.stop();
            LOG.info("PostgreSQL container stopped successfully");
        } catch (Exception e) {
            LOG.error("Error stopping PostgreSQL container", e);
        }
    }

    @Test
    public void whenSaveUser() {
        System.out.println(POSTGRES.getJdbcUrl());
        System.out.println(POSTGRES.getUsername());
        System.out.println(POSTGRES.getPassword());
    }
}
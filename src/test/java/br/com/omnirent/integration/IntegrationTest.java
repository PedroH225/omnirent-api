package br.com.omnirent.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
public abstract class IntegrationTest {

	@Container
    @ServiceConnection
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0.36")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
}

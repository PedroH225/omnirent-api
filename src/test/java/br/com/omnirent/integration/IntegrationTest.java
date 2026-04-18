package br.com.omnirent.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTest {

    static final MySQLContainer mysql =
            new MySQLContainer("mysql:8.0.36")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test");
	
	static {
        mysql.start();
        System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
        System.setProperty("spring.datasource.username", mysql.getUsername());
        System.setProperty("spring.datasource.password", mysql.getPassword());
    }
}

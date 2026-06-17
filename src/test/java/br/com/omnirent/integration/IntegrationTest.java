package br.com.omnirent.integration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@Testcontainers
@ActiveProfiles("test")
public abstract class IntegrationTest {

    static final MySQLContainer mysql =
            new MySQLContainer("mysql:8.0.36")
                    .withDatabaseName("test_db")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);

    static final RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-management")
            .withReuse(true);
    
	static {
        mysql.start();
        rabbit.start();
    }
    
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
    	registry.add("spring.datasource.url", mysql::getJdbcUrl);
    	registry.add("spring.datasource.username", mysql::getUsername);
    	registry.add("spring.datasource.password", mysql::getPassword);

        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }
}

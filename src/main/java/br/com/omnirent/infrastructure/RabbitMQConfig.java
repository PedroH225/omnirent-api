package br.com.omnirent.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    TopicExchange domainExchange() {
        return new TopicExchange("domain.exchange");
    }

    @Bean
    Queue emailQueue() {
        return new Queue("email.queue");
    }

    @Bean
    Binding userRegisteredBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with("user.registered");
    }
}

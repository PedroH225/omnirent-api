package br.com.omnirent.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.common.enums.IntegrationEventRouting;

@Configuration
public class RabbitBindingConfig {

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange("deadLetterExchange");
    }

    @Bean
    Queue dlq() {
        return QueueBuilder.durable("deadLetter.queue").build();
    }

    @Bean
    Binding DLQbinding() {
        return BindingBuilder.bind(dlq())
                .to(deadLetterExchange())
                .with("deadLetter");
    }
    
    @Bean
    TopicExchange domainExchange() {
        return new TopicExchange("domain.exchange");
    }

    @Bean
    Queue emailQueue() {
    	return QueueBuilder.durable("email.queue")
                .withArgument("x-dead-letter-exchange", "deadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }

    @Bean
    Binding userRegisteredBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.USER_REGISTERED.getKey());
    }
    
    @Bean
    Binding userStatusChangedBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.USER_STATUS_CHANGED.getKey());
    }
}

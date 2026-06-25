package br.com.omnirent.infrastructure;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.common.enums.IntegrationEventRouting;

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
    
    @Bean
    Binding newItemChangedBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.NEW_ITEM.getKey());
    }
    
    @Bean
    Binding newRentalBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_CREATED.getKey());
    }
    
    @Bean
    Binding rentalStatusChangeBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_STATUS_CHANGED.getKey());
    }
    
    @Bean
    Binding rentalInUseBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_IN_USE.getKey());
    }
    
    @Bean
    Binding rentalLateBinding(
            Queue emailQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_LATE.getKey());
    }
}

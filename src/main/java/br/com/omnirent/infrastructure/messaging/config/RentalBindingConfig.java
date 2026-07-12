package br.com.omnirent.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.common.enums.IntegrationEventRouting;

@Configuration
public class RentalBindingConfig {

    @Bean
    Queue emailRentalQueue() {
    	return QueueBuilder.durable("email.rental.queue")
                .withArgument("x-dead-letter-exchange", "deadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }
    
    @Bean
    Binding newRentalBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_CREATED.getKey());
    }
    
    @Bean
    Binding rentalStatusChangeBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_STATUS_CHANGED.getKey());
    }
    
    @Bean
    Binding rentalCanceledBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_CANCELED.getKey());
    }
    
    @Bean
    Binding rentalInUseBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_IN_USE.getKey());
    }
    
    @Bean
    Binding rentalExpiredBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_EXPIRED.getKey());
    }
    
    @Bean
    Binding rentalLateBinding(
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_LATE.getKey());
    }
     
}

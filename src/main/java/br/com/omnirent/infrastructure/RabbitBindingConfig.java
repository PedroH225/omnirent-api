package br.com.omnirent.infrastructure;

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
    Queue emailRentalQueue() {
    	return QueueBuilder.durable("email.rental.queue")
                .withArgument("x-dead-letter-exchange", "deadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }
    
    @Bean
    Queue paymentQueue() {
        return QueueBuilder.durable("payment.queue")
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
            Queue emailRentalQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailRentalQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_CREATED.getKey());
    }
    
    @Bean
    Binding paymentRequestedBinding(
            Queue paymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.PAYMENT_REQUESTED.getKey());
    }
    
    @Bean
    Binding paymentExpirationRequestedBinding(
            Queue paymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.PAYMENT_EXPIRATION.getKey());
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
    Binding rentalCanceledPaymentBinding(
            Queue paymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(paymentQueue)
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
    
    @Bean
    Binding rentalLatePaymentBinding(
            Queue paymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_LATE.getKey());
    }
}

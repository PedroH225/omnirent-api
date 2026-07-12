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
public class PaymentBindingConfig {

    @Bean
    Queue emailPaymentQueue() {
    	return QueueBuilder.durable("email.payment.queue")
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
    Binding rentalLatePaymentBinding(
            Queue paymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.RENTAL_LATE.getKey());
    }
    
    @Bean
    Binding paymentCreatedEmailBinding(
            Queue emailPaymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailPaymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.PAYMENT_CREATED.getKey());
    }
    
    @Bean
    Binding paymentConfirmedEmailBinding(
            Queue emailPaymentQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailPaymentQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.PAYMENT_CONFIRMED.getKey());
    }
}

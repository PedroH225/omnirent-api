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
public class ItemBindingConfig {

    @Bean
    Queue emailItemQueue() {
    	return QueueBuilder.durable("email.item.queue")
                .withArgument("x-dead-letter-exchange", "deadLetterExchange")
                .withArgument("x-dead-letter-routing-key", "deadLetter")
                .build();
    }
    
    @Bean
    Binding newItemChangedBinding(
            Queue emailItemQueue,
            TopicExchange domainExchange
    ) {
        return BindingBuilder
                .bind(emailItemQueue)
                .to(domainExchange)
                .with(IntegrationEventRouting.NEW_ITEM.getKey());
    }
}

package br.com.omnirent.infrastructure;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RabbitErrorHandler {
	
	@Autowired
	private MessageConverter jsonMessageConverter;
	
	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
	        ConnectionFactory connectionFactory,
	        MessageConverter jsonMessageConverter) {

	    SimpleRabbitListenerContainerFactory factory =
	            new SimpleRabbitListenerContainerFactory();

	    factory.setConnectionFactory(connectionFactory);
	    factory.setMessageConverter(jsonMessageConverter);

	    factory.setAdviceChain(
	        RetryInterceptorBuilder.stateless()
	            .maxRetries(3)
	            .recoverer((message, cause) -> {
	                Throwable realCause = cause.getCause() != null
	                        ? cause.getCause()
	                        : cause;

	                log.error(
	                    "Message moved to DLQ. Reason: {}",
	                    realCause.getMessage()
	                );
	            })
	            .build()
	    );

	    return factory;
	}
}

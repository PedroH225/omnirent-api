package br.com.omnirent.infrastructure;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.IntegrationEventRouting;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitIntegrationEventListener {

    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handle(IntegrationEvent event) {

        IntegrationEventRouting eventRouting =  IntegrationEventRouting.from(event);;
        
        rabbitTemplate.convertAndSend(
        		"domain.exchange",
        		eventRouting.getKey(),
                event
        );       
    }
}

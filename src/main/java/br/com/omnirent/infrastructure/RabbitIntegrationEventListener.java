package br.com.omnirent.infrastructure;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import br.com.omnirent.common.enums.IntegrationEventRouting;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitIntegrationEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(IntegrationEvent event) {

        IntegrationEventRouting eventRouting =  IntegrationEventRouting.from(event);;
        
        rabbitTemplate.convertAndSend(
        		"domain.exchange",
        		eventRouting.getKey(),
                event
        );       
    }
}

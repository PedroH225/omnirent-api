package br.com.omnirent.common.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher
        implements DomainEventPublisher, SecurityEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(DomainEvent event) {
        publisher.publishEvent(event);
    }
    
    @Override
    public void publish(SecurityEvent event) {
        publisher.publishEvent(event);
    }
}

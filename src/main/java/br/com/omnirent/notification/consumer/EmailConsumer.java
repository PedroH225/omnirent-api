package br.com.omnirent.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.UserNotificationData;
import br.com.omnirent.notification.email.service.EmailService;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.mail.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class EmailConsumer {

    private final EmailService emailService;
        
    private final JpaNotificationQueryRepository queryRepository;
    
    @RabbitHandler
    public void handle(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event);
    }
    
    @RabbitHandler
    public void handle(UserStatusChangeEvent event) {
    	UserNotificationData data = queryRepository.findNotificationData(event.userId())
    			.orElseThrow(() -> new NotificationDataNotException());
;
        emailService.sendUserStatusChanged(data);
    }
    
    @RabbitHandler
    public void handle(ItemCreatedEvent event) {
        emailService.sendNewItemEmail(event);
    }
}

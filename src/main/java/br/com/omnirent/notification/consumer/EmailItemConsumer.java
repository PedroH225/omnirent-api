package br.com.omnirent.notification.consumer;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.AdminNotificationData;
import br.com.omnirent.notification.email.service.ItemEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.mail.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.item.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class EmailItemConsumer {

    private final ItemEmailService emailService;
        
    private final JpaNotificationQueryRepository queryRepository;
    
    @RabbitHandler
    public void handle(ItemCreatedEvent event) {
    	List<AdminNotificationData> adminsData = queryRepository.findAdminsNotificationData();
    	
        emailService.sendNewItemEmail(event);
        
        adminsData.forEach(a -> emailService.notifyAdmin(event, a));
    }
}

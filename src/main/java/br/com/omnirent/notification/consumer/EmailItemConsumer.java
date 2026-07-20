package br.com.omnirent.notification.consumer;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.item.event.ItemRejectedEvent;
import br.com.omnirent.item.event.ItemStatusUpdatedEvent;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.AdminNotificationData;
import br.com.omnirent.notification.context.ItemNotificationData;
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
    
    @RabbitHandler
    public void handle(ItemStatusUpdatedEvent event) {
    	ItemStatus newStatus = event.currentBody().status();
    	ItemNotificationData notificationData = null;
    	
    	switch (newStatus) {
		case AVAILABLE:
			 
			break;
		case UNAVAILABLE:
			
			break;
		case BLOCKED:
			
			break;
		default:
			break;
		}
    }
    
    @RabbitHandler
    public void handle(ItemRejectedEvent event) {
    	ItemNotificationData notificationData = getNotificationData(event.entityId());
    	emailService.sendItemRejectedEmail(event, notificationData);
    }
    
    public ItemNotificationData getNotificationData(String itemId) {
    	return queryRepository.findItemNotificationData(itemId)
    			.orElseThrow(NotificationDataNotException::new);
    }
}

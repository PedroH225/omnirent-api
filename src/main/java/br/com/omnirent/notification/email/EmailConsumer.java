package br.com.omnirent.notification.email;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.queue")
public class EmailConsumer {

    private final EmailService emailService;
    
    private final RentalEmailService rentalEmailService;
    
    @RabbitHandler
    public void handle(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event);
    }
    
    @RabbitHandler
    public void handle(UserStatusChangeEvent event) {
        emailService.sendUserStatusChanged(event);
    }
    
    @RabbitHandler
    public void handle(ItemCreatedEvent event) {
        emailService.sendNewItemEmail(event);
    }
    
    @RabbitHandler
    public void handle(RentalCreatedEvent event) {
    	rentalEmailService.sendRentalCreatedToOwner(event);
    	rentalEmailService.sendRentalCreatedToUser (event);
    }
}

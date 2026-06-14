package br.com.omnirent.email;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.omnirent.security.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "email.queue")
    public void handle(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event);
    }
}

package br.com.omnirent.payment;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.payment.event.PaymentExpirationRequestEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
	
	private final SpringDomainEventPublisher eventPublisher;
	
	private final PaymentQueryRepository queryRepository;
	
	private final Clock clock;

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void markExpiredPayments() {
		//Instant threshold = ZonedDateTime.now(clock).minusSeconds(1).toInstant();
		Instant threshold = ZonedDateTime.now(clock).minusMinutes(30).toInstant();

		List<String> expiredIds = 
				queryRepository.findExpiredIds(PaymentStatus.PENDING, threshold);
		
		for (String id : expiredIds) {
			eventPublisher.publish(new PaymentExpirationRequestEvent(id));
		}
	}
}



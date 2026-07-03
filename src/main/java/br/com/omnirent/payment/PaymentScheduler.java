package br.com.omnirent.payment;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.context.PaymentExpiredContext;
import br.com.omnirent.rental.RentalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
	
	private final PaymentService paymentService;
			
	private final PaymentQueryRepository queryRepository;
	
	private final Clock clock;

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void markExpiredPayments() {
		Instant threshold = ZonedDateTime.now(clock).minusMinutes(30).toInstant();

		List<String> expiredIds = 
				queryRepository.findExpiredIds(PaymentStatus.PENDING, threshold);
		
		for (String id : expiredIds) {
			paymentService.expirePayment(id);
		}
	}
}



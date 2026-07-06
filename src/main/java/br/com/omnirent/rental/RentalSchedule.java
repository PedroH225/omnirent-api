package br.com.omnirent.rental;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.rental.context.RentalInUseAuditSnapshot;
import br.com.omnirent.rental.context.RentalInUseContext;
import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.event.RentalLateEvent;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RentalSchedule {
	
	private RentalRepository rentalRepository;
	
	private RentalQueryRepository queryRepository;
	
	private SpringDomainEventPublisher eventPublisher;
	
	private RentalService rentalService;
	
	private Clock clock;

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void updateLateRentals() {
		List<String> lateRentalsIds = queryRepository.findLateRentals(RentalStatus.IN_USE);

		if (!lateRentalsIds.isEmpty()) {
			rentalRepository.markLate(RentalStatus.LATE, RentalStatus.IN_USE);
			
			lateRentalsIds.stream()
			.map(RentalLateEvent::new)
			.forEach(eventPublisher::publish);
		}			
	}
	
	@Transactional
	@Scheduled(fixedRate = 30000)
	public void updateShippedRentals() {
		Instant threshold = ZonedDateTime.now(clock).minusHours(1).toInstant();
		
		List<RentalInUseContext> shippedRentals = queryRepository
				.findShippedAfterThreshold(RentalStatus.SHIPPED, threshold);

		rentalService.markInUse(shippedRentals);
		
		List<RentalStatusChangeContext> returnShippedRentals = queryRepository
				.findReturnShippedAfterThreshold(RentalStatus.RETURN_SHIPPED, threshold);
		
		rentalService.markReturned(returnShippedRentals);
	}
}

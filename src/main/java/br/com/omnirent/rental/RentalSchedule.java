package br.com.omnirent.rental;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
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
		Instant threshold = Instant.now().minusSeconds(10);
		
		List<RentalStatusChangeContext> shippedRentals = queryRepository
				.findShippedAfterThreshold(RentalStatus.SHIPPED, threshold);
		System.out.println(shippedRentals);
		rentalService.markInUse(shippedRentals);
		
		List<RentalStatusChangeContext> returnShippedRentals = queryRepository
				.findShippedAfterThreshold(RentalStatus.RETURN_SHIPPED, threshold);
		
		rentalService.markReturned(returnShippedRentals);
	}
}

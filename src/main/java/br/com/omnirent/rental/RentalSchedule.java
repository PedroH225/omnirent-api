package br.com.omnirent.rental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.event.RentalLateEvent;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RentalSchedule {
	
	private RentalRepository rentalRepository;
	
	private RentalQueryRepository queryRepository;
	
	private SpringDomainEventPublisher eventPublisher;

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
}

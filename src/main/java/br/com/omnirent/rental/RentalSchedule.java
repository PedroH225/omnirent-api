package br.com.omnirent.rental;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.rental.domain.Rental;
import jakarta.transaction.Transactional;

@Component
public class RentalSchedule {
	
	@Autowired
	public RentalRepository rentalRepository;

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void updateLateRentals() {
		List<Rental> rentals = rentalRepository.
				findByRentalStatusAndEndDateBefore(RentalStatus.IN_USE, LocalDateTime.now());
		
		rentals.forEach(Rental::markLate);
	}
}

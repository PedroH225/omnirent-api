package br.com.omnirent.rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import jakarta.transaction.Transactional;

@Component
public class RentalSchedule {
	
	@Autowired
	public RentalRepository rentalRepository;

	@Transactional
	@Scheduled(fixedRate = 30000)
	public void updateLateRentals() {
		rentalRepository.markLate(RentalStatus.LATE, RentalStatus.IN_USE);
	}
}

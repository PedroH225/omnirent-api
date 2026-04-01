package br.com.omnirent.rental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RentalSchedule {
	
	@Autowired
	public RentalRepository rentalRepository;

}

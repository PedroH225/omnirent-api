package br.com.omnirent.rental;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {

	private RentalRepository rentalRepository;
	
	public Rental findById(String id) {
		Optional<Rental> rental = rentalRepository.findById(id);
		
		if (rental.isEmpty()) {
			throw new RuntimeException("Rental not found.");
		}
		
		return rental.get();
	}
}

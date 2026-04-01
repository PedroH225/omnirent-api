package br.com.omnirent.rental;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.rental.domain.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

	List<Rental> findByRentalStatusAndEndDateBefore
	(RentalStatus rentalStatus, LocalDateTime endDate);
}

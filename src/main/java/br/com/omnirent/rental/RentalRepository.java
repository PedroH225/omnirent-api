package br.com.omnirent.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.omnirent.rental.domain.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

}

package br.com.omnirent.rental;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.rental.domain.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {
	
	@Modifying
	@Query("""
			UPDATE Rental r
			SET r.rentalStatus = :status
			WHERE r.id = :id
			""")
	void updateRentalStatus(@Param("id")String rentalId, RentalStatus status);
	
	@Modifying
	@Query("""
			UPDATE Rental r
			SET r.rentalStatus = :status, r.startDate = :startDate, r.endDate = :endDate
			WHERE r.id = :id
			""")
	void updateRentalPeriodAndStatus
	(@Param("id")String rentalId, RentalStatus status, LocalDateTime startDate, LocalDateTime endDate);

	@Modifying
	@Query("""
			UPDATE Rental r
			SET r.rentalStatus = :late
			WHERE r.rentalStatus = :inUse AND r.endDate < CURRENT_TIMESTAMP
			""")
	void markLate(@Param("late")RentalStatus late, @Param("inUse")RentalStatus inUse);
	
	List<Rental> findByRentalStatusAndEndDateBefore
	(RentalStatus rentalStatus, LocalDateTime endDate);
}

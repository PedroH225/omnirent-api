package br.com.omnirent.rental;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalDetailDTO;

@Repository
public interface RentalRepository extends JpaRepository<Rental, String> {

	List<Rental> findByRentalStatusAndEndDateBefore
	(RentalStatus rentalStatus, LocalDateTime endDate);
	
	@Query("""
			SELECT new br.com.omnirent.rental.dto.RentalDetailDTO(r.id, r.startDate, r.endDate,
			r.finalPrice, r.rentalStatus, r.rentalPeriod,
			new br.com.omnirent.user.dto.UserResponseDTO(rt.id, rt.username),
			new br.com.omnirent.user.dto.UserResponseDTO(o.id, o.username),
			new br.com.omnirent.item.dto.ItemSnapshotDTO(iSnp.id, iSnp.name, iSnp.itemData.brand,
			iSnp.itemData.model,iSnp.itemData.basePrice, iSnp.itemData.itemCondition, iSnp.itemData.description),
			new br.com.omnirent.address.dto.AddressSnapshotDTO(aSnp.id, aSnp.addressData.street, aSnp.addressData.number,
			aSnp.addressData.complement, aSnp.addressData.district, aSnp.addressData.city,
			aSnp.addressData.state, aSnp.addressData.country, aSnp.addressData.zipCode))
			FROM Rental r 
			JOIN r.renter rt JOIN r.owner o JOIN r.itemSnapshot iSnp JOIN r.addressSnapshot aSnp
			WHERE r.id = :id 
			""")
	Optional<RentalDetailDTO> findRentalDetail(String id);
}

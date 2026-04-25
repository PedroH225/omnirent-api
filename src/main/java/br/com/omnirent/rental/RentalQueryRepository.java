package br.com.omnirent.rental;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;

public interface RentalQueryRepository extends Repository<Rental, String>  {
	
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
	
	@Query("""
			SELECT new br.com.omnirent.rental.dto.RentalDisplayDTO(r.id, r.startDate, r.endDate,
			r.finalPrice, r.rentalStatus, r.rentalPeriod, i.id, i.name, r.renterId,
			rt.name, r.ownerId, o.name, r.createdAt)
			FROM Rental r JOIN r.itemSnapshot i JOIN r.renter rt JOIN r.owner o
			WHERE r.id = :id
			""")
	Optional<RentalDisplayDTO> findRentalDisplayDTO(@Param("id") String rentalId);
	
	@Query("""
			SELECT new br.com.omnirent.rental.dto.RentalDisplayDTO(r.id, r.startDate, r.endDate,
			r.finalPrice, r.rentalStatus, r.rentalPeriod, i.id, i.name, r.renterId,
			rt.name, r.ownerId, o.name, r.createdAt)
			FROM Rental r JOIN r.itemSnapshot i JOIN r.renter rt JOIN r.owner o
			WHERE rt.id = :id
			""")
	List<RentalDisplayDTO> findUserRented(@Param("id") String renterId);
	
	@Query("""
			SELECT new br.com.omnirent.rental.dto.RentalDisplayDTO(r.id, r.startDate, r.endDate,
			r.finalPrice, r.rentalStatus, r.rentalPeriod, i.id, i.name, r.renterId,
			rt.name, r.ownerId, o.name, r.createdAt)
			FROM Rental r JOIN r.itemSnapshot i JOIN r.renter rt JOIN r.owner o
			WHERE o.id = :id
			""")
	List<RentalDisplayDTO> findUserRentals(@Param("id") String ownerId);
	
	@Query("""
			SELECT new br.com.omnirent.rental.context.RentalStatusChangeContext
			(r.id, r.ownerId, r.renterId, r.rentalStatus, r.rentalPeriod)
			FROM Rental r
			WHERE r.id = :id
			""")
	 Optional<RentalStatusChangeContext> getStatusChangeContext(@Param("id")String rentalId);
}

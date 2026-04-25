package br.com.omnirent.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressResponseDTO;


@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

	@Query("""
			SELECT new br.com.omnirent.address.dto.AddressResponseDTO(a.id, ad.street, ad.number,
			ad.complement, ad.district, ad.city, ad.state, ad.country, ad.zipCode, a.createdAt, a.updatedAt)
			FROM Address a JOIN a.user u JOIN a.addressData ad WHERE u.id = :id
			""")
	List<AddressResponseDTO> findAddressByUser(String id);
	
	@Query("""
			SELECT COUNT(a) > 0 FROM Address a 
			JOIN a.user u
			WHERE a.id = :id AND u.id = :userId
			""")
	Boolean verifyAddress(@Param("id")String addressId, @Param("userId")String userId);
}

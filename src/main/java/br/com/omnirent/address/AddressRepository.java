package br.com.omnirent.address;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

	@Query("""
			SELECT new br.com.omnirent.address.AddressResponseDTO(a.id, ad.street, ad.number,
			ad.complement, ad.district, ad.city, ad.state, ad.country, ad.zipCode, a.createdAt, a.updatedAt)
			FROM Address a JOIN a.user u JOIN a.addressData ad WHERE u.id = :id
			""")
	List<AddressResponseDTO> findAddressByUser(String id);
}

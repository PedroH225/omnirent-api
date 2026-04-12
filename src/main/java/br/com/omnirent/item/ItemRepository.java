package br.com.omnirent.item;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemDetailDTO;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

	@Query("""
			SELECT new br.com.omnirent.item.domain.ItemDetailDTO(i.id, i.name, i.itemData.brand,
			i.itemData.model, i.itemData.description, i.itemData.basePrice,
			i.itemData.itemCondition, i.itemStatus,
			new br.com.omnirent.category.SubCategoryResDTO(sc.id, sc.name, c.name),
			new br.com.omnirent.address.AddressResponseDTO(a.id, ad.street, ad.number,
			ad.complement, ad.district, ad.city, ad.state, ad.country, ad.zipCode, a.createdAt, a.updatedAt),
			new br.com.omnirent.user.domain.UserResponseDTO(o.id, o.username),
			i.createdAt, i.updatedAt)
			FROM Item i 
			JOIN i.owner o JOIN i.subCategory sc JOIN sc.category c JOIN i.pickupAddress a 
			JOIN a.addressData ad
			WHERE i.id = :id
			""")
	Optional<ItemDetailDTO> findItemDetailDTO(String id);
}

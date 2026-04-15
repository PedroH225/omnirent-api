package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

	@Query("""
			SELECT new br.com.omnirent.item.dto.ItemDetailDTO(i.id, i.name, i.itemData.brand,
			i.itemData.model, i.itemData.description, i.itemData.basePrice,
			i.itemData.itemCondition, i.itemStatus,
			new br.com.omnirent.category.dto.SubCategoryResDTO(sc.id, sc.name, c.name),
			new br.com.omnirent.address.dto.AddressResponseDTO(a.id, ad.street, ad.number,
			ad.complement, ad.district, ad.city, ad.state, ad.country, ad.zipCode, a.createdAt, a.updatedAt),
			new br.com.omnirent.user.dto.UserResponseDTO(o.id, o.username),
			i.createdAt, i.updatedAt)
			FROM Item i 
			JOIN i.owner o JOIN i.subCategory sc JOIN sc.category c JOIN i.pickupAddress a 
			JOIN a.addressData ad
			WHERE i.id = :id
			""")
	Optional<ItemDetailDTO> findItemDetailDTO(String id);
	
	@Query("""
			SELECT new br.com.omnirent.item.dto.ItemDisplayDTO(i.id, i.name, i.itemData.basePrice,
			i.itemData.itemCondition, i.itemStatus, sc.name, i.createdAt,
			new br.com.omnirent.user.dto.UserResponseDTO(o.id, o.username))
			FROM Item i
			JOIN i.owner o JOIN i.subCategory sc
			WHERE o.id = :id
			""")
	List<ItemDisplayDTO> findUserItems(@Param("id")String userId);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.ItemRentedContext(
			new br.com.omnirent.item.context.ItemInfo(i.id, i.name, i.itemData.brand, 
			i.itemData.model, i.itemData.description, i.itemData.basePrice, i.itemData.itemCondition),
			new br.com.omnirent.address.context.AddressInfo(ad.id, ad.addressData.number, 
			ad.addressData.complement, ad.addressData.district, ad.addressData.city, ad.addressData.state, ad.addressData.country, ad.addressData.zipCode),
			o.id, o.name)
			FROM Item i JOIN i.pickupAddress ad JOIN i.owner o
			WHERE i.id = :id
			""")
	Optional<ItemRentedContext> getItemRentedContext(@Param("id")String itemId);
}

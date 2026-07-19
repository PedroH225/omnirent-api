package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ChangeItemSubCategoryContext;
import br.com.omnirent.item.context.ItemFeedContext;
import br.com.omnirent.item.context.ItemFeedFilter;
import br.com.omnirent.item.context.ItemPermissionData;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.context.UpdateItemStatusContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;

public interface ItemQueryRepository extends Repository<Item, String> {
		
		@Query(value ="""
				SELECT new br.com.omnirent.item.context.ItemFeedContext(
				i.id, i.name, i.itemData.itemCondition, i.itemData.basePrice,
				sc.name, i.createdAt, 
				new br.com.omnirent.user.dto.UserResponseDTO(o.id, o.username),
				im.storageKey)
				FROM Item i JOIN i.owner o JOIN i.subCategory sc JOIN sc.category c
				LEFT JOIN i.images im
				WHERE (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
				  AND (:category IS NULL OR c.name = :category)
				  AND (:subCategory IS NULL OR sc.name = :subCategory)
				  AND (:itemCondition IS NULL OR i.itemData.itemCondition = :itemCondition)
				  AND (im IS NULL OR im.displayOrder = 0)
					""", countQuery = """
							    SELECT COUNT(DISTINCT i)
							    FROM Item i
							    JOIN i.owner o
							    JOIN i.subCategory sc
							    JOIN sc.category c
							    LEFT JOIN i.images im
							    WHERE (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
							      AND (:category IS NULL OR c.name = :category)
							      AND (:subCategory IS NULL OR sc.name = :subCategory)
							      AND (:itemCondition IS NULL OR i.itemData.itemCondition = :itemCondition)
							      AND (im IS NULL OR im.displayOrder = 0)
							""")
		Page<ItemFeedContext> getFeedContexts(
				String name, String category, String subCategory, ItemCondition itemCondition,
				Pageable pageable);
		
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
			new br.com.omnirent.address.context.AddressInfo(ad.id, ad.addressData.street, ad.addressData.number, 
			ad.addressData.complement, ad.addressData.district, ad.addressData.city, ad.addressData.state, ad.addressData.country, ad.addressData.zipCode),
			o.id, o.name)
			FROM Item i JOIN i.pickupAddress ad JOIN i.owner o
			WHERE i.id = :id
			""")
	Optional<ItemRentedContext> getItemRentedContext(@Param("id")String itemId);

	@Query("""
			SELECT new br.com.omnirent.item.context.UpdateItemContext(
			new br.com.omnirent.item.context.ItemInfo(i.id, i.name, i.itemData.brand, 
			i.itemData.model, i.itemData.description, i.itemData.basePrice, i.itemData.itemCondition),
			i.ownerId, i.pickupAddressId, i.subCategoryId, i.itemStatus)
			FROM Item i 
			WHERE i.id = :id
			""")
	Optional<UpdateItemContext> getUpdateContext(@Param("id")String itemId);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.UpdateItemStatusContext(
			i.id, i.itemStatus, i.ownerId, o.userStatus)
			FROM Item i JOIN i.owner o
			WHERE i.id = :id
			""")
	Optional<UpdateItemStatusContext> getUpdateStatusContext(@Param("id")String itemId);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.ChangeItemAddressContext(
			i.id, i.ownerId, i.pickupAddressId, i.itemStatus)
			FROM Item i
			WHERE i.id = :id
			""")
	Optional<ChangeItemAddressContext> getChangeAddressContext(@Param("id")String id);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.ChangeItemSubCategoryContext(
			i.id, i.ownerId, i.subCategoryId, i.itemStatus)
			FROM Item i
			WHERE i.id = :id
			""")
	Optional<ChangeItemSubCategoryContext> getChangeSubCategoryContext(@Param("id")String id);

	@Query("""
			SELECT new br.com.omnirent.item.context.ItemPermissionData(
			i.itemStatus, i.ownerId, o.userStatus)
			FROM Item i JOIN i.owner o
			WHERE i.id = :itemId
			""")
	Optional<ItemPermissionData> getPermissionData(String itemId);
}

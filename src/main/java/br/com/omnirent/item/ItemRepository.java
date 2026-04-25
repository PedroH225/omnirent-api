package br.com.omnirent.item;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {	
	@Modifying
	@Query("""
			UPDATE Item i
			SET i.name = :itemName, i.itemData.brand = :brand, 
				i.itemData.model = :model, i.itemData.description = :description,
				i.itemData.basePrice = :basePrice, i.itemData.itemCondition = :itemCondition,
			    i.updatedAt = CURRENT_TIMESTAMP
			WHERE i.id = :id
			""")
	int updateItem(
			@Param("id") String id, @Param("itemName") String itemName, @Param("brand") String brand,
			@Param("model") String model, @Param("description") String description, @Param("basePrice") BigDecimal basePrice,
			@Param("itemCondition") ItemCondition itemCondition
	);
	
	@Modifying
	@Query("""
			UPDATE Item i SET i.itemStatus = :status, updatedAt = CURRENT_TIMESTAMP 
			WHERE i.id = :id AND i.itemStatus = :current
			""")
	int updateStatus(@Param("id")String itemId, @Param("current")ItemStatus currentStatus, @Param("status")ItemStatus status);

	@Modifying
	@Query("""
			UPDATE Item i SET i.pickupAddressId = :addressId, updatedAt = CURRENT_TIMESTAMP
			WHERE i.id = :id AND i.itemStatus = :status AND i.pickupAddressId = :currentAddressId
			""")
	int updatePickupAddress(@Param("id")String itemId, @Param("addressId")String addressId,
			@Param("currentAddressId")String currentAddress, @Param("status")ItemStatus status);
}

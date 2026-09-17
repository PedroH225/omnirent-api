package br.com.omnirent.item;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.context.ItemImageResponseDTO;
import br.com.omnirent.item.domain.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

	List<ItemImage> findByItemId(String itemId);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.ItemImageResponseDTO(
			im.id, im.storageKey, im.displayOrder, im.itemId)
			FROM ItemImage im
			WHERE im.itemId = :itemId
			ORDER BY im.displayOrder ASC
			""")
	List<ItemImageResponseDTO> findItemImages(String itemId);
	
	@Query("""
		    SELECT new br.com.omnirent.item.context.ItemImageResponseDTO(
		        im.id, im.storageKey, im.displayOrder, im.itemId)
		    FROM ItemImage im
		    WHERE im.itemId IN :itemIds
		    ORDER BY im.itemId, im.displayOrder ASC
		    """)
		List<ItemImageResponseDTO> findItemImagesByItemIds(
				@Param("itemIds") List<String> itemIds);
}

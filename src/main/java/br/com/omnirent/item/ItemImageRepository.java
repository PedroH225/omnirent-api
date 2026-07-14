package br.com.omnirent.item;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.context.ItemImageResponseDTO;
import br.com.omnirent.item.domain.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

	List<ItemImage> findByItemId(String itemId);
	
	@Query("""
			SELECT new br.com.omnirent.item.context.ItemImageResponseDTO(
			im.id, im.storageKey, im.displayOrder)
			FROM ItemImage im
			WHERE im.itemId = :itemId
			ORDER BY im.displayOrder ASC
			""")
	List<ItemImageResponseDTO> findItemImages(String itemId);
}

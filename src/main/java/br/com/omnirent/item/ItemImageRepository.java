package br.com.omnirent.item;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.omnirent.item.domain.ItemImage;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, UUID> {

	List<ItemImage> findByItemId(String itemId);
}

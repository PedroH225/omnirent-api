package br.com.omnirent.factory;

import java.time.Instant;
import java.util.UUID;

import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemImage;
import br.com.omnirent.item.domain.ItemImageRequestDto;

public final class ItemImageTestFactory {

	private ItemImageTestFactory() {}
	
	private static final String PATH = "/item";
	
	public static ItemImage createPersisted(Item item, Integer order, Instant instant) {
		UUID uuid = UUID.randomUUID();
		String key = String.format("%s/%s", PATH, uuid);
		return new ItemImage(uuid, key, order, instant, item, item.getId());
	}
	
	public static ItemImageRequestDto createRequest(
			UUID id, String tempId, Integer order) {
		return new ItemImageRequestDto(id, tempId, order);
		
	}
}

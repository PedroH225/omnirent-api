package br.com.omnirent.item;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ForbiddenException;
import br.com.omnirent.item.domain.Item;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemAuthorizationService {
	
	private final String DEFAULT_MESSAGE = "You are not allowed to perform this operation.";

	public void requireOwner(Item item, String currentUserId) {
		if (!item.getOwnerId().equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
}

package br.com.omnirent.item;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ForbiddenException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemAuthorizationService {
	
	private final String DEFAULT_MESSAGE = "You are not allowed to perform this operation.";

	public void requireOwner(String itemId, String currentUserId) {
		if (!itemId.equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
}

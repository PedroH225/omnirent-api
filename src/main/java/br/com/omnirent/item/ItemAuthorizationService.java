package br.com.omnirent.item;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ForbiddenException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemAuthorizationService {
	
	private final String DEFAULT_MESSAGE = "You are not allowed to perform this operation.";

	private final String BLOCKED_MESSAGE = "Item is blocked by the system.";

	public void requireOwner(String ownerId, String currentUserId) {
		if (!ownerId.equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
	
	public void requireNotBlocked(ItemStatus currentStatus) {
		if (currentStatus == ItemStatus.BLOCKED) {
			throw new ForbiddenException(BLOCKED_MESSAGE);
		}
	}
}

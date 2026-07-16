package br.com.omnirent.item;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ItemErrorType;
import br.com.omnirent.item.context.ItemPermissionData;
import br.com.omnirent.user.UserAuthorizationService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemAuthorizationService {
	
	private final ItemQueryRepository itemQueryRepository;
	
	private final UserAuthorizationService userAuthorizationService;
	
	public void validateItemFromDB(String itemId, String currUser) {
		ItemPermissionData permissionData = itemQueryRepository.getPermissionData(itemId)
				.orElseThrow();
		requireOwner(permissionData.ownerId(), currUser);
		userAuthorizationService.requireNotBanned(permissionData.ownerStatus());
		requireNotBlocked(permissionData.itemStatus());
	}
	
	public void requireOwner(String ownerId, String currentUserId) {
		if (!ownerId.equals(currentUserId)) {
			throw new ApiException(ItemErrorType.OWNER_REQUIRED);
		}
	}
	
	public void requireNotBlocked(ItemStatus currentStatus) {
		if (currentStatus == ItemStatus.BLOCKED) {
			throw new ApiException(ItemErrorType.BLOCKED);
		}
	}
}

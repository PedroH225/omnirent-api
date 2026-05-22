package br.com.omnirent.rental.domain;

import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.RentalErrorType;

@Service
public class RentalAuthorizationService {	
	public void requireOne(Set<String> actors, String currentUserId) {
		if (!actors.contains(currentUserId)) {
			throw new ApiException(RentalErrorType.OPERATION_FORBIDDEN);
		}
	}
}

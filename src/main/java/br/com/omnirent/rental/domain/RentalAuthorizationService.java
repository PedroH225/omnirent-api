package br.com.omnirent.rental.domain;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ForbiddenException;

@Service
public class RentalAuthorizationService {
	
	private final String DEFAULT_MESSAGE = "You are not allowed to perform this operation.";
		
	public void requireOwner(String targetId, String currentUserId) {
		if (!targetId.equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
	
	public void requireRenter(Rental rental, String currentUserId) {
		if (!rental.getRenterId().equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
	
	public void requireOne(Rental rental, String currentUserId) {
		if (!rental.getRenterId().equals(currentUserId) &&
				!rental.getOwnerId().equals(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
}

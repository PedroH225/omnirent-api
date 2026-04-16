package br.com.omnirent.rental.domain;

import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ForbiddenException;

@Service
public class RentalAuthorizationService {
	
	private final String DEFAULT_MESSAGE = "You are not allowed to perform this operation.";
	
	public void requireOne(Set<String> actors, String currentUserId) {
		if (!actors.contains(currentUserId)) {
			throw new ForbiddenException(DEFAULT_MESSAGE);
		}
	}
}

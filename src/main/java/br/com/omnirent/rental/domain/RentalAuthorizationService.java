package br.com.omnirent.rental.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.RentalErrorType;
import br.com.omnirent.rental.RentalQueryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentalAuthorizationService {	
	
	private final RentalQueryRepository queryRepository;
	
	private final Clock clock;
	
	private final ZoneId zoneId;
	
	public void requireOne(Set<String> actors, String currentUserId) {
		if (!actors.contains(currentUserId)) {
			throw new ApiException(RentalErrorType.OPERATION_FORBIDDEN);
		}
	}

	public void canCreateRental(String userId, String itemId) {
		Instant thresold = ZonedDateTime.now(clock).minusMinutes(30).toInstant();
		
		Optional<Instant> optExpired = queryRepository.canCreateRental(
				userId, itemId, RentalStatus.EXPIRED, thresold);
		
		if (optExpired.isPresent()) {
			ZonedDateTime when = 
					optExpired.get().atZone(zoneId).plusMinutes(30);
			throw new ApiException(RentalErrorType.CREATION_COOLDOWN, when);
		}
	}
}

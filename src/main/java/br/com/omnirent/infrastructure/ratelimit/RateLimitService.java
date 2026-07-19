package br.com.omnirent.infrastructure.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;

import br.com.omnirent.common.formatter.DurationMessage;
import br.com.omnirent.common.formatter.DurationMessageUtil;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.RateLimitErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {
	
	private final Cache<String, ClientRateLimitState> cache;
	
	private final RateLimitProperties properties;
	
	private final Clock clock;
	
	private final MessageService messageService;
	
	public void verifyRequest(ClientIdentifier clientIdentifier, RateLimitStrategy strategy) {
		Instant now = clock.instant();
		ClientRateLimitState state = getClientState(clientIdentifier, now);
		int maxRequests = resolveMaxRequests(clientIdentifier.type(), strategy);

		checkBlocked(state, now);

		Duration elapsed = Duration.between(state.getWindowStart(), now);
		
		if (elapsed.compareTo(properties.window()) >= 0) {
			state.resetState(now);
		}
		
		state.incrementRequest();
		
		if (state.getRequestCount() > maxRequests) {
		    applyPenalty(state, now);
			throwManyRequestsWithDuration(state, now);
		}
		log.debug(
			    "RateLimitState[id={}, requests={}, windowStart={}, penaltyLevel={}, blockedUntil={}]",
			    clientIdentifier.identifier(),
			    state.getRequestCount(),
			    state.getWindowStart(),
			    state.getPenaltyLevel(),
			    state.getBlockedUntil()
			);
	}

	private ClientRateLimitState getClientState(ClientIdentifier clientIdentifier, Instant now) {
	    return cache.get(
	        clientIdentifier.identifier(),
	        key -> ClientRateLimitState.createState(now)
	    );
	}
	

	private void checkBlocked(ClientRateLimitState state, Instant now) {
		boolean blocked = state.getBlockedUntil() != null
		        && now.isBefore(state.getBlockedUntil());

		if (blocked) {
			throwManyRequestsWithDuration(state, now);
		}
	}
	
	private void applyPenalty(ClientRateLimitState state, Instant now) {
		int level = Math.min(
			    state.getPenaltyLevel(),
			    properties.penalties().size() - 1);

	    Duration duration = properties.penalties().get(level);

	    state.setBlockedUntil(now.plus(duration));

	    state.incrementPenaltyLevel(properties.penalties().size() - 1);
	}
	
	private Duration getRemainingTime(Instant end, Instant now) {
	    return Duration.between(now, end);
	}

	private int resolveMaxRequests(ClientIdentifierType type, RateLimitStrategy strategy) {
	    return switch (type) {
	        case USER -> strategy.getUserMaxRequests();
	        case IP -> strategy.getIpMaxRequests();
	    };
	}
	
	private void throwManyRequestsWithDuration(ClientRateLimitState state, Instant now) {
		DurationMessage durationMessage = 
				DurationMessageUtil.resolveMessage(
						getRemainingTime(state.getBlockedUntil(), now)
						);
		String timeUnitKey = messageService.get(durationMessage.messageKey());
	    
		throw new ApiException(RateLimitErrorType.TOO_MANY_REQUESTS, 
	    		durationMessage.value(), timeUnitKey);
	}
}

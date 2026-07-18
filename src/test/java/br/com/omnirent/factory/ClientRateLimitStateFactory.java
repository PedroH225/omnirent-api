package br.com.omnirent.factory;

import java.time.Instant;

import br.com.omnirent.infrastructure.ratelimit.ClientRateLimitState;

public final class ClientRateLimitStateFactory {

	private ClientRateLimitStateFactory() {}

	public static ClientRateLimitState newState(Instant now) {
		ClientRateLimitState clientRateLimitState = ClientRateLimitState
				.createState(now);
		return clientRateLimitState;
	}
	
	public static ClientRateLimitState blocked(Instant now) {
		ClientRateLimitState clientRateLimitState = ClientRateLimitState
				.createState(now);
		
		clientRateLimitState.setBlockedUntil(now.plusSeconds(60));
		return clientRateLimitState;
	}
	
	public static ClientRateLimitState withRequests(int requestCount, Instant now) {
		ClientRateLimitState clientRateLimitState = ClientRateLimitState
				.createState(now);
		
		for (int i = 0; i < requestCount; i++) {
			clientRateLimitState.incrementRequest();
		}
		
		return clientRateLimitState;
	}
	
	public static ClientRateLimitState withPenalty(int penaltyLevel, int maxPenalty, Instant now) {
		ClientRateLimitState clientRateLimitState = ClientRateLimitState
				.createState(now);
		
		for (int i = 0; i < penaltyLevel; i++) {
			clientRateLimitState.incrementPenaltyLevel(maxPenalty);
		}
		
		return clientRateLimitState;
	}

	public static ClientRateLimitState withRequestsAndPenalty(
			int requestCount, int penaltyLevel, int maxPenalty, Instant now) {
		ClientRateLimitState clientRateLimitState = ClientRateLimitState
				.createState(now);
		
		for (int i = 0; i < penaltyLevel; i++) {
			clientRateLimitState.incrementPenaltyLevel(maxPenalty);
		}
		
		for (int i = 0; i < requestCount; i++) {
			clientRateLimitState.incrementRequest();
		}
		
		return clientRateLimitState;
	}
}

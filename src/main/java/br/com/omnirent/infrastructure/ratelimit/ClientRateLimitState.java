package br.com.omnirent.infrastructure.ratelimit;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientRateLimitState {

	private int requestCount;
	
	private Instant windowStart;
	
	private int penaltyLevel;
	
	private Instant blockedUntil;

	public static ClientRateLimitState createState(Instant windowStart) {
		ClientRateLimitState state = new ClientRateLimitState();
		
		state.requestCount = 0;
		state.windowStart = windowStart;
		state.penaltyLevel = 0;
		state.blockedUntil = null;
		
		return state;
	}
	
	public void resetState(Instant windowStart) {
		this.requestCount = 0;
		this.windowStart = windowStart;
		this.blockedUntil = null;
	}
	
	public void incrementRequest() {
		requestCount++;
	}

	public void setBlockedUntil(Instant blockedUntil) {
		this.blockedUntil = blockedUntil;
	}

	public void incrementPenaltyLevel(int maxPenaltyLevel) {
	    if (penaltyLevel < maxPenaltyLevel) {
	        penaltyLevel++;
	    }
	}
	
}

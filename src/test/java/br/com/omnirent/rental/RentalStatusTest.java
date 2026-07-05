package br.com.omnirent.rental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.omnirent.common.enums.RentalStatus;
import static br.com.omnirent.common.enums.RentalStatus.*;

public class RentalStatusTest {

	@Test
	void shouldMatchExpectedTransitionsExactly() {
		Map<RentalStatus, Set<RentalStatus>> expected = Map.ofEntries(
			    Map.entry(CREATED, Set.of(CONFIRMED, CANCELLED, EXPIRED)),
			    Map.entry(CONFIRMED, Set.of(PREPARING, CANCELLED)),
			    Map.entry(PREPARING, Set.of(SHIPPED)),
			    Map.entry(SHIPPED, Set.of(IN_USE)),
			    Map.entry(IN_USE, Set.of(RETURN_REQUESTED, LATE)),
			    Map.entry(RETURN_REQUESTED, Set.of(RETURN_SHIPPED)),
			    Map.entry(RETURN_SHIPPED, Set.of(RETURNED)),
			    Map.entry(RETURNED, Set.<RentalStatus>of()),
			    Map.entry(CANCELLED, Set.<RentalStatus>of()),
			    Map.entry(LATE, Set.of(IN_USE)),
			    Map.entry(EXPIRED, Set.of())
			);

	    for (RentalStatus status : RentalStatus.values()) {
	        assertThat(status.getAllowedTransitions())
	            .as("Transitions for " + status)
	            .containsExactlyInAnyOrderElementsOf(expected.get(status));
	    }
	}
	
	@Test
	void shouldAllowValidTransitions() {
		for (RentalStatus source : RentalStatus.values()) {
			for (RentalStatus target : source.getAllowedTransitions()) {
				assertThat(source.canTransition(target)).isTrue();
			}
		}
	}

	@Test
	void shouldRejectInvalidTransitions() {
		for (RentalStatus source : RentalStatus.values()) {
			for (RentalStatus target : RentalStatus.values()) {
				if (!source.getAllowedTransitions().contains(target)) {
					assertThat(source.canTransition(target)).isFalse();
				}
			}
		}
	}

}

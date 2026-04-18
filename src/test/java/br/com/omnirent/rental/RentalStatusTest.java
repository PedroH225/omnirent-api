package br.com.omnirent.rental;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.IllegalRentalStateException;

public class RentalStatusTest {

	@Test
	void shouldAllowValidTransitions() {
		for (RentalStatus source : RentalStatus.values()) {
			for (RentalStatus target : source.getAllowedTransitions()) {
				assertThatCode(() -> source.validateTransition(target)).doesNotThrowAnyException();
			}
		}
	}

	@Test
	void shouldRejectInvalidTransitions() {
		for (RentalStatus source : RentalStatus.values()) {
			for (RentalStatus target : RentalStatus.values()) {
				if (!source.getAllowedTransitions().contains(target)) {
					assertThatThrownBy(() -> source.validateTransition(target))
							.isInstanceOf(IllegalRentalStateException.class);
				}
			}
		}
	}

}

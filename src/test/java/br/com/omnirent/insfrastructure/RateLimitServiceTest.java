package br.com.omnirent.insfrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.benmanes.caffeine.cache.Cache;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.factory.ClientIdentifierFactoryTest;
import br.com.omnirent.factory.ClientRateLimitStateFactory;
import br.com.omnirent.infrastructure.ratelimit.ClientIdentifier;
import br.com.omnirent.infrastructure.ratelimit.ClientRateLimitState;
import br.com.omnirent.infrastructure.ratelimit.RateLimitProperties;
import br.com.omnirent.infrastructure.ratelimit.RateLimitService;
import br.com.omnirent.infrastructure.ratelimit.RateLimitStrategy;

@ExtendWith(MockitoExtension.class)
public class RateLimitServiceTest {
	
	@InjectMocks
	private RateLimitService rateLimitService;
	
	@Mock
	private Cache<String, ClientRateLimitState> cache;
	
	@Mock
	private RateLimitProperties properties;

	@Mock
	private MessageService messageService;

	@Mock
	private Clock clock;
	
	private Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.parse("2026-07-18T10:00:00Z");
        
        lenient().when(clock.instant()).thenReturn(now);
        lenient().when(properties.window()).thenReturn(Duration.ofMinutes(1));
        lenient().when(properties.penalties()).thenReturn(List.of(
                Duration.ofMinutes(1),
                Duration.ofMinutes(5),
                Duration.ofMinutes(15)
        ));        
    }
    
    @Test
    void shouldAllowRequestWhenClientIsBelowConfiguredLimit() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.newState(now);
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        assertThatCode(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
                .doesNotThrowAnyException();

        assertThat(state.getRequestCount()).isEqualTo(1);
    }

    @Test
    void shouldBlockClientWhenRequestLimitIsExceeded() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.withRequests(20, now);
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        assertThatThrownBy(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
                .isInstanceOf(ApiException.class);

        assertThat(state.getBlockedUntil()).isEqualTo(now.plus(Duration.ofMinutes(1)));
        assertThat(state.getPenaltyLevel()).isEqualTo(1);
    }

    @Test
    void shouldResetStateWhenConfiguredWindowExpires() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.withRequests(15, now);
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        Instant later = now.plusSeconds(120);
        when(clock.instant()).thenReturn(later);

        assertThatCode(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
                .doesNotThrowAnyException();

        assertThat(state.getRequestCount()).isEqualTo(1);
        assertThat(state.getWindowStart()).isEqualTo(later);
    }

    @Test
    void shouldApplyProgressivePenalties() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.withRequestsAndPenalty(
        		20, 1, 2, now);
        
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        assertThatThrownBy(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
        	.isInstanceOf(ApiException.class);

        assertThat(state.getBlockedUntil()).isEqualTo(now.plus(Duration.ofMinutes(5)));
        assertThat(state.getPenaltyLevel()).isEqualTo(2);
   
    }

    @Test
    void shouldNotApplyAnotherPenaltyWhileClientIsAlreadyBlocked() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.blocked(now);
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        assertThatThrownBy(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
                .isInstanceOf(ApiException.class);

        assertThat(state.getBlockedUntil()).isEqualTo(now.plusSeconds(60));
        assertThat(state.getPenaltyLevel()).isZero();
    }

    @Test
    void shouldAllowRequestAfterBlockExpires() {
        ClientIdentifier client = ClientIdentifierFactoryTest.user();
        ClientRateLimitState state = ClientRateLimitStateFactory.blocked(now.minusSeconds(120));
        when(cache.get(eq(client.identifier()), any())).thenReturn(state);
        
        assertThatCode(() -> rateLimitService.verifyRequest(client, RateLimitStrategy.DEFAULT))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldHandleDifferentClientIdentifierTypes() {
        ClientIdentifier ipClient = ClientIdentifierFactoryTest.ip();
        ClientRateLimitState ipState = ClientRateLimitStateFactory.withRequests(25, now);
        when(cache.get(eq(ipClient.identifier()), any())).thenReturn(ipState);

        assertThatCode(() -> rateLimitService.verifyRequest(ipClient, RateLimitStrategy.DEFAULT))
                .doesNotThrowAnyException();

        ClientIdentifier userClient = ClientIdentifierFactoryTest.user();
        ClientRateLimitState userState = ClientRateLimitStateFactory.withRequests(20, now);
        when(cache.get(eq(userClient.identifier()), any())).thenReturn(userState);

        assertThatThrownBy(() -> rateLimitService.verifyRequest(userClient, RateLimitStrategy.DEFAULT))
                .isInstanceOf(RuntimeException.class);
    }
	
}

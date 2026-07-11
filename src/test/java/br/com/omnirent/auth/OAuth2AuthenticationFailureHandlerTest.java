package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;

import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.auth.provider.OAuth2AuthenticationFailureHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class OAuth2AuthenticationFailureHandlerTest {

	@InjectMocks
	private OAuth2AuthenticationFailureHandler failureHandler;
	
	@Mock
	private ApiErrorResponseWriter apiWriter;
	
	@Mock
	private AppProperties appProperties;
	
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;
	
	@Test
	void shouldHandleApiExceptionCause() throws IOException, ServletException {
		ApiException apiException = new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
		AuthenticationException ex = new AuthenticationServiceException("Wrapped exception", apiException);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue()).isSameAs(apiException);
		verify(response).sendRedirect("http://localhost:3000/login?error=" + apiException.getErrorCode());
	
		verifyNoMoreInteractions(apiWriter);
	}
}

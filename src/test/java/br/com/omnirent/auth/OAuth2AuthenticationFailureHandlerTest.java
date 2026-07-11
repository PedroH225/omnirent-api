package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

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
	
	@Test
	void shouldHandleOAuthAccessDenied() throws IOException, ServletException {
		OAuth2Error error = new OAuth2Error("access_denied");
		OAuth2AuthenticationException ex = new OAuth2AuthenticationException(error);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
        	.isEqualTo(AuthenticationErrorType.OAUTH_ACCESS_DENIED.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldHandleOAuthProviderUnavailable() throws IOException, ServletException {
		OAuth2Error error = new OAuth2Error("server_error");
		OAuth2AuthenticationException ex = new OAuth2AuthenticationException(error);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.OAUTH_PROVIDER_UNAVAILABLE.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldHandleOAuthAuthenticationFailedForUnknownError() throws IOException, ServletException {
		OAuth2Error error = new OAuth2Error("unknown_error");
		OAuth2AuthenticationException ex = new OAuth2AuthenticationException(error);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.OAUTH_AUTHENTICATION_FAILED.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldHandleInternalAuthenticationServiceException() throws IOException, ServletException {
		InternalAuthenticationServiceException ex = new InternalAuthenticationServiceException("Internal error");
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldHandleAuthenticationServiceException() throws IOException, ServletException {
		AuthenticationServiceException ex = new AuthenticationServiceException("Auth service error");
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.AUTHENTICATION_SERVICE_ERROR.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldHandleGenericAuthenticationException() throws IOException, ServletException {
		BadCredentialsException ex = new BadCredentialsException("Bad credentials");
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		failureHandler.onAuthenticationFailure(request, response, ex);

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());

		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorCode());
		verify(response).sendRedirect("http://localhost:3000/login?error=" + captor.getValue().getErrorCode());
	}
	
	@Test
	void shouldNotPropagateExceptionWhenSendRedirectThrowsIOException() throws IOException, ServletException {
		BadCredentialsException ex = new BadCredentialsException("Bad credentials");
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		ArgumentCaptor<ApiException> captor = ArgumentCaptor.forClass(ApiException.class);
		doThrow(new IOException("Redirect failed")).when(response).sendRedirect("http://localhost:3000/login?error=INVALID_CREDENTIALS");

		assertThrowsExactly(IOException.class, () -> failureHandler.onAuthenticationFailure(request, response, ex));

		verify(apiWriter).onApiError(eq(request), eq(response), captor.capture());
		assertThat(captor.getValue().getErrorCode())
			.isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorCode());
	}
}

package br.com.omnirent.security.auth.provider;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

	private ApiErrorResponseWriter apiWriter;
	
	private AppProperties appProperties;
    	
	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request, 
			HttpServletResponse response,
			AuthenticationException ex
			) throws IOException, ServletException {
		ApiException apiException =
                ex.getCause() instanceof ApiException ae
                        ? ae
                        : resolveApiError(ex);

        apiWriter.onApiError(request, response, apiException);
        
		response.sendRedirect(String.format("%s/login?error=%s", 
				appProperties.frontUrl(), apiException.getErrorCode()));
	}
	
	private ApiException resolveApiError(AuthenticationException ex) {
	    if (ex.getCause() instanceof ApiException ae) {
	        return ae;
	    }
		if (ex instanceof OAuth2AuthenticationException oauthEx) {
			return resolveOauthError(oauthEx);
		} 
		if(ex instanceof InternalAuthenticationServiceException) {
			return new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
		}
		if (ex instanceof AuthenticationServiceException) {
			return new ApiException(AuthenticationErrorType.AUTHENTICATION_SERVICE_ERROR);
		}
	    return new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
	}
	
	private ApiException resolveOauthError(AuthenticationException ex) {
		OAuth2AuthenticationException oauthError = (OAuth2AuthenticationException) ex;
		String errorCode = oauthError.getError().getErrorCode();
		return switch (errorCode) {
		    case "access_denied" ->
		        new ApiException(AuthenticationErrorType.OAUTH_ACCESS_DENIED);
	
		    case "invalid_token_response",
		         "invalid_user_info_response",
		         "server_error",
		         "temporarily_unavailable" ->
		        new ApiException(AuthenticationErrorType.OAUTH_PROVIDER_UNAVAILABLE);
	
		    default ->
		        new ApiException(AuthenticationErrorType.OAUTH_AUTHENTICATION_FAILED);
		};
	}
}

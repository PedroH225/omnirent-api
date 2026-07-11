package br.com.omnirent.security.auth.provider.mapper;

import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.auth.provider.records.GithubEmailMetadata;
import br.com.omnirent.security.auth.provider.records.GithubEmailResponse;

@Component
public class GithubEmailClient {

    private final RestClient restClient;

    public GithubEmailClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.github.com")
                .build();
    }

    public GithubEmailMetadata findPrimaryEmail(String accessToken) {
    	try {
        GithubEmailResponse[] emails = restClient.get()
                .uri("/user/emails")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + accessToken)
                .retrieve()
                .body(GithubEmailResponse[].class);

        return Arrays.stream(emails)
                .filter(GithubEmailResponse::primary)
                .findFirst()
                .map(email -> new GithubEmailMetadata(
                        email.email(),
                        email.verified()))
                .orElseThrow(() ->
                        new ApiException(AuthenticationErrorType.OAUTH_EMAIL_REQUIRED));
    	} 
    	catch (HttpClientErrorException.Unauthorized e) {
    	    throw new ApiException(AuthenticationErrorType.OAUTH_AUTHENTICATION_FAILED);
    	}
    	catch (HttpClientErrorException.Forbidden e) {
    	    throw new ApiException(AuthenticationErrorType.OAUTH_ACCESS_DENIED);
    	}
    	catch (HttpClientErrorException e) {
    	    throw new ApiException(AuthenticationErrorType.OAUTH_PROVIDER_UNAVAILABLE);
    	}
    	catch (RestClientException e) {
    	    throw new ApiException(AuthenticationErrorType.OAUTH_PROVIDER_UNAVAILABLE);
    	}	
    }
    	
}


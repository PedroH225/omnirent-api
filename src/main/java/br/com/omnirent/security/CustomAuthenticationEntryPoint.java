package br.com.omnirent.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiErrorResponse;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    private MessageService messageService;
	
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex)
            throws IOException {
    	ApiException apiException =
                ex.getCause() instanceof ApiException ae
                        ? ae
                        : new ApiException(AuthenticationErrorType.AUTHENTICATION_REQUIRED);
        
    	String localizedMessage = messageService.get(
        		apiException.getMessageKey(), apiException.getArgs());
       
        ApiErrorResponse body = new ApiErrorResponse(
                Instant.now(),
                apiException.getHttpStatus().value(),
                apiException.getErrorType(),
                apiException.getErrorCode(),
                localizedMessage,
                request.getRequestURI()
        );

        response.setStatus(apiException.getHttpStatus().value());
        response.setContentType("application/json");

        response.getWriter().write(
                objectMapper.writeValueAsString(body)
        );
    }
}